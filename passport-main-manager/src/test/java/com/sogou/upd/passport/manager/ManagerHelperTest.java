package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import junit.framework.TestCase;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */
public class ManagerHelperTest extends TestCase {

    private static final int clientId = 2013;

    private static final String serverSecret = "ezr8DRjSn%*[mqa>,$^m6_+r~qSwN3";

    /**
     * 测试生成内部接口code参数
     */
    @Test
    public void testGeneratorCode() {
        long ct = System.currentTimeMillis();
        System.out.println("ct:" + ct);
//        String ct =  "1381915491000";
        String code = ManagerHelper.generatorCodeGBK("shipengzhi1986@sogou.com", 1120, "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY", ct);
        System.out.println("code:" + code);

        try {
            String pwdMD5 = Coder.encryptMD5("spz1986411");
            System.out.println("pwdMD5:" + pwdMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 值输出为空
     */
    @Test
    public void testResult() {
        Result result = new APIResultSupport(false);
        System.out.println(result.getCode());
    }

    public static void main(String args[]) throws Exception {
        new ManagerHelperTest().testGeneratorCode();
    }

    /**
     * 测试生成getpairtoken接口中sig参数
     */
    @Test
    public void testGeneratorSig() {
        String passportId = "shipengzhi1986@sogou.com";
        int clientId = 1044;
        String refreshToken = "6724sEW686c2uJCO5U4755SFB5tJrj";
        long timestamp = 1375250235592l;
        String clientSecret = "=#dW$h%q)6xZB#m#lu'x]]wP=\\FUO7";
        String sigString = passportId + clientId + refreshToken + timestamp + clientSecret;
        String originSig = "2e5cdf5a510affe479a458d1c9ddde79";
        try {
            String actualSig = Coder.encryptMD5(sigString);
            System.out.println("sig:" + actualSig);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testCheck() {
        String str = "232_4e3e_";
        //只含有汉字、数字、字母、下划线，且不能以下划线开头和结尾
        System.out.println(str.matches("^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$"));
        //限制输入含有特定字符的
        //System.out.println(str.matches("^(?!.*搜狗)(?!.*sogou)(?!.*sougou).*$"));
    }

    @Test
    public void testEmail() {
        String email = "t5y-.uku@-163.com";
        String regex = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
        System.out.println(email.matches(regex));
    }

    @Test
    public void testMD5Pwd() {
        String pwd = "123456";
        System.out.println(DigestUtils.md5Hex(pwd.getBytes()));
    }
}
