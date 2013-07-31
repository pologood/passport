package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.OAuthTokenApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 桌面端登录流程Manager
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCAccountManagerImpl implements PCAccountManager {

    private static final long SIG_EXPIRES = 60 * 60 * 1000; //sig里的timestamp有效期，一小时，单位毫秒
    private static final Logger logger = LoggerFactory.getLogger(PCAccountManagerImpl.class);
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private OAuthTokenApiManager proxyOAuthTokenApiManager;
    @Autowired
    private PCAccountTokenService pcAccountService;
    @Autowired
    private AppConfigService appConfigService;

    @Override
    public Result createPairToken(PcPairTokenParams pcTokenParams) {
        Result finalResult = new APIResultSupport(false);
        try {
            int clientId = Integer.parseInt(pcTokenParams.getAppid());
            String passportId = pcTokenParams.getUserid();
            String password = pcTokenParams.getPassword();
            String instanceId = pcTokenParams.getTs();
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            if (!Strings.isNullOrEmpty(password)) {   //校验用户名和密码
                AuthUserApiParams authUserApiParams = new AuthUserApiParams(clientId, passportId, password);
                Result result = proxyLoginApiManager.webAuthUser(authUserApiParams);
                if (!result.isSuccess()) {
                    return result;
                }
            } else {    //校验签名
                String sig = pcTokenParams.getSig();
                String timestamp = pcTokenParams.getTimestamp();
                String clientSecret = appConfig.getClientSecret();
                if (!verifySig(passportId, clientId, instanceId, timestamp, clientSecret, sig)) {
                    finalResult.setCode(ErrorUtil.ERR_SIGNATURE_OR_TOKEN);
                    return finalResult;
                }
            }
            AccountToken accountToken = pcAccountService.initialAccountToken(passportId, instanceId, appConfig);
            if (accountToken != null) {
                finalResult.setSuccess(true);
                finalResult.setDefaultModel(accountToken);
            } else {
                finalResult.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
            }
            return finalResult;
        } catch (Exception e) {
            logger.error("createPairToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    @Override
    public Result authRefreshToken(PcRefreshTokenParams pcRefreshTokenParams) {

        Result result = new APIResultSupport(false);
        int clientId = Integer.parseInt(pcRefreshTokenParams.getAppid());
        String passportId = pcRefreshTokenParams.getUserid();
        String instanceId = pcRefreshTokenParams.getTs();
        String refreshToken = pcRefreshTokenParams.getRefresh_token();
        try {
            Result verifyRTResult = proxyOAuthTokenApiManager.refreshToken(pcRefreshTokenParams);
            if (!verifyRTResult.isSuccess() && !pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken)) {
                result.setCode(ErrorUtil.ERR_REFRESH_TOKEN);
                return result;
            }
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            AccountToken accountToken = pcAccountService.updateOrInsertAccountToken(passportId, instanceId, appConfig);
            if (accountToken != null) {
                result.setSuccess(true);
                result.setDefaultModel(accountToken);
            } else {
                result.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
            }
            return result;
        } catch (Exception e) {
            logger.error("authRefreshToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result authToken(PcAuthTokenParams authPcTokenParams) {
        Result result = new APIResultSupport(false);
        try {
            //验证accessToken
            int clientId = Integer.parseInt(authPcTokenParams.getAppid());
            String passportId = authPcTokenParams.getUserid();
            String instanceId = authPcTokenParams.getTs();
            if (pcAccountService.verifyAccessToken(passportId, clientId, instanceId, authPcTokenParams.getToken())) {
                result.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error("authToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    /*
     * 校验签名，算法：sig=MD5(passportId + clientId + refresh_token + timestamp + clientSecret）
     */
    private boolean verifySig(String passportId, int clientId, String instanceId, String timestamp, String clientSecret, String sig) throws Exception {
        // 校验时间戳
        long curTimestamp = System.currentTimeMillis();
        long ts = Long.parseLong(timestamp);
        if (curTimestamp > ts + SIG_EXPIRES) {
            return false;
        }
        // TODO 调用sohu的根据userid获取refreshToken接口
        AccountToken accountToken = pcAccountService.queryAccountToken(passportId, clientId, instanceId);
        if (accountToken == null) {
            return false;
        }
        String refreshToken = accountToken.getRefreshToken();
        if (!isValidToken(accountToken.getRefreshValidTime())) {
            return false;
        }
        String sigString = passportId + clientId + refreshToken + timestamp + clientSecret;
        String actualSig = Coder.encryptMD5(sigString);
        return actualSig.equalsIgnoreCase(sig);
    }

    /**
     * 验证Token是否失效
     */
    private boolean isValidToken(long tokenValidTime) {
        long currentTime = System.currentTimeMillis();
        return tokenValidTime > currentTime;
    }

}