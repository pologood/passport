package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.RegisterApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.RegMobileApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.RegMobileCaptchaApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyRegisterApiManager")
public class ProxyRegisterApiManagerImpl extends BaseProxyManager implements RegisterApiManager {

    private static Logger log = LoggerFactory.getLogger(ProxyRegisterApiManagerImpl.class);

    @Override
    public Result regMailUser(RegEmailApiParams regEmailApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result regMobileUser(RegMobileApiParams regMobileApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        Result result = new APIResultSupport(false);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(regMobileCaptchaApiParams);
            result = executeResult(requestModelXml);
        } catch (Exception e) {
            log.error("mobile register phone account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMoblieApiParams baseMoblieApiParams) {
        Result result = new APIResultSupport(false);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(baseMoblieApiParams);
            result = executeResult(requestModelXml);
        } catch (Exception e) {
            log.error("mobile register phone account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }
}
