package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.impl.SGLoginApiManagerImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class SGLoginApiManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private LoginApiManager sgLoginApiManager;

    private static final int clientId = 1100;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("13126693178");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("123456"));
            authUserParameters.setUsertype(1);
            Result result = sgLoginApiManager.webAuthUser(authUserParameters);
            System.out.println("testAuthUser:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCookieInfo() {
        String userid = "shipengzhi1986@sogou.com";
        int client_id = 1120;
        String uniqname = "跳刀的兔子";
        String refnick = "跳刀的兔子";
        // userid, client_id, ru, ip, uniqname, refnick
        CookieApiParams cookieApiParams = new CookieApiParams(userid, client_id, "", "", uniqname, refnick);
        Result result = sgLoginApiManager.getCookieInfo(cookieApiParams);
        System.out.println("sginf: " + result.getModels().get("sginf"));
        System.out.println("sgrdig: " + result.getModels().get("sgrdig"));
    }

}
