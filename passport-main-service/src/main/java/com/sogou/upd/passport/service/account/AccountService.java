package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    /**
     * 注册时检查手机号，发送验证码是否正确
     *
     * @param account
     * @return
     */
    public boolean checkSmsInfoFromCache(String account, String smsCode, String clientId);

    /**
     * 检查缓存中是否存在此key
     *
     * @param account
     * @return
     */
    public boolean checkCacheKeyIsExist(String account);

    /**
     * 重发验证码时更新缓存状态
     *
     * @param cacheKey
     * @return
     */
    public Result updateSmsCacheInfoByKeyAndClientId(String cacheKey, int clientId);

    /**
     * 手机验证码的获取与重发
     *
     * @param account
     * @return
     */
    public Result handleSendSms(String account, int clientId);

    /**
     * 初始化非第三方用户账号
     *
     * @param username
     * @param password
     * @param ip
     * @param provider
     * @return
     */
    public Account initialAccount(String username, String password, String ip, int provider) throws SystemException;

    /**
     * 初始化第三方用户账号
     *
     * @param connectUid
     * @param ip
     * @param provider
     * @return
     */
    public Account initialConnectAccount(String connectUid, String ip, int provider) throws SystemException;

    /**
     * 根据用户名获取Account对象
     * @param username
     * @return
     */
    public Account getAccountByUserName(String username);

    /**
     * 验证账号的有效性，是否为正常用户
     * @param userId
     * @return 验证不通过，则返回null
     */
    public Account verifyAccountVaild(long userId);

    /**
     * 验证用户名密码是否正确
     *
     * @param username
     * @param password
     * @return 用户名或密码不匹配，则返回null
     */
    public Account verifyUserPwdVaild(String username, String password);

    /**
     * 根据主键ID获取passportId
     *
     * @param passportId
     * @return
     */
    public long getUserIdByPassportId(String passportId);


    /**
     * 注册成功后清除sms缓存信息
     *
     * @param mobile
     * @param clientId
     * @return
     */
    public boolean deleteSmsCache(final String mobile, final String clientId);

    /**
     * 重置密码
     * @param mobile
     * @param password
     * @return
     */
    public Account resetPassword(String mobile,String password) throws SystemException;
}
