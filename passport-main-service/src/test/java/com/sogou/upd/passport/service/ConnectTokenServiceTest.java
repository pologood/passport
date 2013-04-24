package com.sogou.upd.passport.service;

import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class ConnectTokenServiceTest extends AbstractJUnit4SpringContextTests {

    private static final String passportId = "13621009174@sohu.com";
    private static final int provider = 4;
    private static final String appKey = "1279688155";

    @Inject
    private ConnectTokenService accountConnectService;

    //根据userId获取openId
    @Test
    public void testGetOpenIdByQuery() {
        String openid = accountConnectService.querySpecifyOpenId(passportId, provider, appKey);
        System.out.println("openid:" + openid);
    }
}
