package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;

import java.util.Map;

/**
 * QQ.web正确返回结果为text/html
 * 异常结果为(callback：{json})
 * QQ.wap正常和异常返回结果均为text/html
 * 响应码为200
 * 变态！变态！变态！变态！
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + HttpConstant.ContentType.JSON + " encoded");
        }
    }

    /**
     * QQ Authoz Code不返回openid，需额外接口调用
     *
     * @return
     */
    @Override
    public String getOpenid() {
        // QQ不返回openid，需调用获取openid的接口
        return "";
    }

    /**
     * QQ Authoz Code不返回nickName
     *
     * @return ""
     */
    @Override
    public String getNickName() {
        return "";
    }

}
