package com.sogou.upd.passport.oauth2.openresource.dataobject;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-28
 * Time: 下午9:08
 * To change this template use File | Settings | File Templates.
 */
public class SinaOAuthTokenDO {

    private String access_token;
    private long expires_in;
    private String remind_in;
    private String openid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRemind_in() {
        return remind_in;
    }

    public void setRemind_in(String remind_in) {
        this.remind_in = remind_in;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}