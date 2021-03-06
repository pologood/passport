package com.sogou.upd.passport.common;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {

    /**
     * 生成第三方授权中的state参数cookie，防止CRSF攻击
     *
     * @param providerStr
     * @return
     */
    public static String constructStateCookieKey(String providerStr) {
        return providerStr + "_state";
    }

    /**
     * clientId=1044为浏览器PC端、浏览器移动端、输入法PC端
     * clientId=1105为输入法MAC
     *
     * @param clientId
     * @return
     */
    public static boolean isIePinyinToken(int clientId) {
        return clientId == CommonConstant.PC_CLIENTID || clientId == CommonConstant.PINYIN_MAC_CLIENTID;
    }

    /**
     * clientId=1044为浏览器PC端、浏览器移动端、输入法PC端
     *
     * @param clientId
     * @return
     */
    public static boolean isExplorerToken(int clientId) {
        return clientId == CommonConstant.PC_CLIENTID;
    }

    /**
     * clientId=1105为输入法MAC
     *
     * @param clientId
     * @return
     */
    public static boolean isPinyinMACToken(int clientId) {
        return clientId == CommonConstant.PINYIN_MAC_CLIENTID;
    }

    /**
     * 判断时间戳（毫秒）是否有效
     *
     * @param ct
     * @return
     */
    public static boolean isMillCtValid(long ct) {
        long currentTime = System.currentTimeMillis();
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM_IN_MILLI;
        return timeRight;
    }

    /**
     * 判断时间戳（秒）是否有效
     *
     * @param ct
     * @return
     */
    public static boolean isSecCtValid(long ct) {
        long currentTime = System.currentTimeMillis() / 1000;
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM;
        return timeRight;
    }

    /**
     * 浏览器新版本采用新的UI设计，此方法判断是否为新UI的版本
     * 分隔版本为：5.1.7.14966
     *
     * @param version
     * @return
     */
    public static boolean isNewVersionSE(String version) {
        try {
            if (!Strings.isNullOrEmpty(version)) {
                String[] versionArray = version.split("\\.");
                if (versionArray.length != 4) {
                    return false;
                }
                if (Integer.parseInt(versionArray[0]) > 5) {
                    return true;
                } else if (Integer.parseInt(versionArray[0]) < 5) {
                    return false;
                }
                if (Integer.parseInt(versionArray[1]) > 1) {
                    return true;
                } else if (Integer.parseInt(versionArray[1]) < 1) {
                    return false;
                }
                if (Integer.parseInt(versionArray[2]) > 7) {
                    return true;
                } else if (Integer.parseInt(versionArray[2]) < 7) {
                    return false;
                }
                if (Integer.parseInt(versionArray[3]) > 14966) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
