package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;

/**
 * 登录相关
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:19
 */
public interface LoginApiManager {

    /**
     * web端校验用户名和密码是否正确
     * 密码必须为MD5
     *
     * @param authUserApiParams
     * @return
     */
    public Result webAuthUser(AuthUserApiParams authUserApiParams);

    /**
     * 适用于手机应用使用第三方登录的场景，用户登录完成之后，
     * 会通过302重定向的方式将token带给产品的服务器端，
     * 产品的服务器端通过传入passportid和token验证用户的合法性，且token具有较长的有效期。
     *
     * @param token
     * @return
     */
    public Result appAuthToken(String token);

    /**
     * 构造sohu生成并设置cookie的url
     *
     * @param createCookieUrlApiParams
     * @return
     */
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps);

    /**
     * 生成的cookie值
     * sohu的是通过搜狐内部接口getcookieinfo获取ppinf和pprdig
     * sogou的是自己生成sginf和sgrdig
     *
     * @param cookieApiParams
     * @return
     */
    public Result getCookieInfo(CookieApiParams cookieApiParams);

    /**
     * 生成cookie的值
     * <p/>
     * 生成ver=5 版本的 ppinf 、pprdig、passport 、ppinfo
     *
     * @param cookieApiParams
     * @return
     */
    public Result getSGCookieInfoForAdapter(CookieApiParams cookieApiParams);
}
