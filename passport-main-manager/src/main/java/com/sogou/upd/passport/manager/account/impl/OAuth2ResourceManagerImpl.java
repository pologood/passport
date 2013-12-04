package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.SHPlusConstant;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 采用OAuth2协议访问受保护数据
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OAuth2ResourceManagerImpl implements OAuth2ResourceManager {

    private Logger log = LoggerFactory.getLogger(OAuth2ResourceManagerImpl.class);
    private static final Logger shPlusTokenLog = LoggerFactory.getLogger("shPlusTokenLogger");

    public static final String DATA = "data";
    public static final String RESOURCE = "resource";
    public static final String SNAME = "sname";
    public static final String SID = "sid";



    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;
    @Autowired
    private SHPlusTokenService shPlusTokenService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    SnamePassportMappingService snamePassportMappingService;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;

    @Override
    public Result resource(PCOAuth2ResourceParams params) {
        Result result = new OAuthResultSupport(false);
        int clientId = params.getClient_id();
        String instanceId = params.getInstance_id();
        String accessToken = params.getAccess_token();
        try {
            clientId = clientId == 30000004 ? CommonConstant.PC_CLIENTID : clientId;  //兼容浏览器PC端sohu+接口
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENT);
                return result;
            }
            String clientSecret = appConfig.getClientSecret();
            String resourceType = params.getResource_type();
            if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_COOKIE)) {
                result = getCookieValue(accessToken, clientId, clientSecret, instanceId);
            } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {
                result = getFullUserInfo(accessToken, clientId, clientSecret, instanceId);
            } else {
                result.setCode(ErrorUtil.INVALID_RESOURCE_TYPE);
                return result;
            }
        } catch (Exception e) {
            log.error("Obtain OAuth2 Resource Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /**
     * 获取cookie值
     *
     * @return
     */
    @Override
    public Result getCookieValue(String accessToken, int clientId, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Result cookieResult;
        Map resourceMap = Maps.newHashMap();
        String passportId = null;
        try {
            passportId = getPassportIdByToken(accessToken, clientId, clientSecret, instanceId);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams(passportId,
                    CommonConstant.DEFAULT_CONNECT_REDIRECT_URL, 1, "sogou.com");
            if (CommonHelper.isBuildNewCookie()) {
                cookieResult = sgLoginApiManager.getCookieValue(createCookieUrlApiParams);
            } else {
                //生成cookie
                CookieApiParams cookieApiParams = new CookieApiParams();
                cookieApiParams.setUserid(passportId);
                cookieApiParams.setClient_id(clientId);
                cookieApiParams.setRu(CommonConstant.DEFAULT_CONNECT_REDIRECT_URL);
                cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
                cookieApiParams.setPersistentcookie(String.valueOf(1));
                cookieResult = proxyLoginApiManager.getSHCookieValue(cookieApiParams);

            }
            if (!cookieResult.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                return result;
            }
            String suffix = ";path=/;domain=.sogou.com;expires=Tuesday, 17-Sep-15 19:02:21 GMT";   // TODO 这里不能写死有效期，要改
            String ppinf = cookieResult.getModels().get("ppinf") + suffix;
            String pprdig = cookieResult.getModels().get("pprdig") + suffix;
            String[] cookieArray = new String[]{"ppinf=" + ppinf, "pprdig=" + pprdig};
            resourceMap.put("msg", "get cookie success");
            resourceMap.put("code", 0);
            resourceMap.put("scookie", cookieArray);

            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get cookie value fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    @Override
    public Result queryPassportIdByAccessToken(String token, int clientId, String instanceId) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            String passportId = getPassportIdByToken(token, clientId, appConfig.getClientSecret(), instanceId);
            if (Strings.isNullOrEmpty(passportId)) {
                finalResult.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return finalResult;
            }
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(passportId);
            return finalResult;
        } catch (Exception e) {
            log.error("createToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    private String getPassportIdByToken(String accessToken, int clientId, String clientSecret, String instanceId) {
        Map resourceMap = Maps.newHashMap();
        String passportId = null;
        if (accessToken.startsWith(CommonConstant.SG_TOKEN_START)) {
            passportId = pcAccountTokenService.getPassportIdByToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                //校验accessToken
                if (!pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken)) {
                    return null;
                }
            }
        } else {
            //sohu+token，获取passportId
            Map map = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_FULL_USERINFO);
            if (SHPlusConstant.AUTH_TOKEN_SUCCESS.equals(map.get("result"))) {
                resourceMap = (Map) map.get(RESOURCE);
                Map dataMap = (Map) resourceMap.get(DATA);
                String sname = (String) dataMap.get(SNAME);
                String sid = (String) dataMap.get(SID);
                passportId = snamePassportMappingService.queryPassportIdBySid(sid);
                //处理11.26号数据迁移以后注册的账号
                if (StringUtils.isBlank(passportId)) {
                    passportId = sname + "@sogou.com";
                }
            }
            shPlusTokenLog.info("[SHPlusToken] get shplus cookie by accesstoken,accessToken：" + accessToken);
        }
        return passportId;
    }

    /**
     * 获取完整的个人信息
     *
     * @return
     */
    @Override
    public Result getFullUserInfo(String accessToken, int clientId, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = getPassportIdByToken(accessToken, clientId, clientSecret, instanceId);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            Result getUserInfoResult = getUserInfo(passportId);
            String uniqname = "", large_avatar = "", mid_avatar = "", tiny_avatar = "";
            if (getUserInfoResult.isSuccess()) {
                uniqname = (String) getUserInfoResult.getModels().get("uniqname");
                large_avatar = (String) getUserInfoResult.getModels().get("img_180");
                mid_avatar = (String) getUserInfoResult.getModels().get("img_50");
                tiny_avatar = (String) getUserInfoResult.getModels().get("img_30");
            }
            Map data = Maps.newHashMap();
            data.put("nick", uniqname);
            data.put("large_avatar", large_avatar);
            data.put("mid_avatar", mid_avatar);
            data.put("tiny_avatar", tiny_avatar);
            data.put("sid", passportId);
            resourceMap.put("data", data);
            resourceMap.put("msg", "get full user info success");
            resourceMap.put("code", 0);
            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get full userInfo fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }


    @Override
    public String getUniqname(String passportId) {
        String uniqname = null;
        AccountBaseInfo accountBaseInfo = getBaseInfo(passportId);
        if (accountBaseInfo != null) {
            uniqname = accountBaseInfo.getUniqname();
        }
        uniqname = getAndUpdateUniqname(passportId,accountBaseInfo,uniqname);
        return uniqname;
    }

    @Override
    public String defaultUniqname(String passportId) {
        if (AccountDomainEnum.THIRD == AccountDomainEnum.getAccountDomain(passportId)) {
            return "搜狗用户";
        }
        return passportId.substring(0, passportId.indexOf("@"));
    }

    private Result getUserInfo(String passportId) {
        Result result = new APIResultSupport(false);
        String uniqname = "", large_avatar = "", mid_avatar = "", tiny_avatar = "";
        AccountBaseInfo accountBaseInfo = getBaseInfo(passportId);
        if (accountBaseInfo != null) {
            uniqname = accountBaseInfo.getUniqname();
            Result getPhotoResult = photoUtils.obtainPhoto(accountBaseInfo.getAvatar(), "30,50,180");
            large_avatar = (String) getPhotoResult.getModels().get("img_180");
            mid_avatar = (String) getPhotoResult.getModels().get("img_50");
            tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
        }
        uniqname = getAndUpdateUniqname(passportId,accountBaseInfo,uniqname);
        result.setSuccess(true);
        result.setDefaultModel("uniqname", uniqname);
        result.setDefaultModel("img_30", tiny_avatar);
        result.setDefaultModel("img_50", mid_avatar);
        result.setDefaultModel("img_180", large_avatar);
        return result;
    }

    private String getAndUpdateUniqname(String passportId,AccountBaseInfo accountBaseInfo,String uniqname){
        if (!isValidUniqname(passportId,uniqname)){
            //从论坛获取昵称
            uniqname = pcAccountManager.getBrowserBbsUniqname(passportId);
            if (isValidUniqname(passportId,uniqname)) {
                if (accountBaseInfo != null) {
                    accountBaseInfoService.updateUniqname(accountBaseInfo, uniqname);
                } else {
                    accountBaseInfoService.insertAccountBaseInfo(passportId, uniqname, "");
                }
            }
        }
        if (!isValidUniqname(passportId,uniqname)) {
            uniqname = defaultUniqname(passportId);
        }
        return uniqname;
    }

    private boolean isValidUniqname(String passportId,String uniqname){
        if (Strings.isNullOrEmpty(uniqname) || uniqname.equals(passportId.substring(0, passportId.indexOf("@")))){
            return false;
        }
        return true;
    }
    private AccountBaseInfo getBaseInfo(String passportId) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setUserid(passportId);
        Result getUserInfoResult = shPlusUserInfoApiManager.getUserInfo(infoApiparams);
        if (getUserInfoResult.isSuccess()) {
            Object obj = getUserInfoResult.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo = null;
            if (obj != null) {
                accountBaseInfo = (AccountBaseInfo) obj;
            }
            return accountBaseInfo;
        }
        return null;
    }

}
