package com.sogou.upd.passport.web.inteceptor;

import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截所有请求，通过cookie验证用户是否已经登录
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:14
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

//    @Autowired
//    private CommonManager commonManager;

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler) throws java.lang.Exception {
        String userid= request.getHeader(LoginConstant.USER_ID_HEADER);
        if (StringUtil.isBlank(userid)){
            return true;
        }
//        if (commonManager.isAccountExists(userid)){
//            hostHolder.setPassportId(userid);
//        }
        hostHolder.setPassportId(userid);
        return true;
    }
}