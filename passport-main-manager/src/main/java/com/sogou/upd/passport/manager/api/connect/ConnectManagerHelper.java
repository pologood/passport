package com.sogou.upd.passport.manager.api.connect;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-30
 * Time: 上午1:24
 * To change this template use File | Settings | File Templates.
 */
public class ConnectManagerHelper {

    public static String constructRedirectURI(int clientId, String ru, String type, String instanceId, String pCallbackUrl, String ip, String from) {
        try {
            ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
            Map<String, Object> callbackParams = Maps.newHashMap();
            callbackParams.put(CommonConstant.CLIENT_ID, clientId);
            callbackParams.put(CommonConstant.RESPONSE_RU, ru);
            callbackParams.put("type", type);
            callbackParams.put("ip", ip);
            callbackParams.put("ts", instanceId);
            if(!Strings.isNullOrEmpty(from)){
                callbackParams.put("from", from);
            }
            StringBuffer query = new StringBuffer(OAuthUtils.format(callbackParams.entrySet(), CommonConstant.DEFAULT_CONTENT_CHARSET));
            return pCallbackUrl + "?" + query;
        } catch (UnsupportedEncodingException e) {
            return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
    }
}
