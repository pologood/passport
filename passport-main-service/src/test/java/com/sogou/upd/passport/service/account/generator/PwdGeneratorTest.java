package com.sogou.upd.passport.service.account.generator;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午2:34
 * To change this template use File | Settings | File Templates.
 */
public class PwdGeneratorTest extends BaseGeneratorTest {

    @Test
    public void testPwdGenerator() {
        try {
            String pwdSign = PwdGenerator.generatorPwdSign(PASSWORD);
            System.out.println(pwdSign);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
