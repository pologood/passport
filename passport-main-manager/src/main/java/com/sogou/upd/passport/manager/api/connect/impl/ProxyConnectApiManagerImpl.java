package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.OAuthParametersApplier;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:28
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyConnectApiManager")
public class ProxyConnectApiManagerImpl implements ConnectApiManager {

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip) throws OAuthProblemException {
        String providerStr = AccountTypeEnum.getProviderStr(provider);

        Map params = Maps.newHashMap();
        params.put("provider", providerStr);
        params.put("appid", SHPPUrlConstant.APP_ID);
        params.put("ru", connectLoginParams.getRu());
        params.put("display", connectLoginParams.getDisplay());
        params.put("type", connectLoginParams.getType());
        params.put("forcelogin", connectLoginParams.isForce());

        String url = QueryParameterApplier.applyOAuthParametersString(SHPPUrlConstant.CONNECT_LOGIN_ULR, params);
        return url;
    }
}