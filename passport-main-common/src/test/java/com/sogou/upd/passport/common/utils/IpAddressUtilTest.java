package com.sogou.upd.passport.common.utils;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:21 To change this template use
 * File | Settings | File Templates.
 */
public class IpAddressUtilTest extends TestCase {
    private static String IP = "202.114.164.64";

    @Test
    public void testGetCity() {
        System.out.println(IpAddressUtil.getCity(IP));
    }
}
