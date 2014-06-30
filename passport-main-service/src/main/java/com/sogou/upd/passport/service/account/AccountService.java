package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22 Time: 下午3:38
 */
public interface AccountService {

    /**
     * 初始化web用户账号
     */
    public Account initialWebAccount(String username, String ip)
            throws ServiceException;

    /**
     * 初始化非第三方用户账号
     */
    public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider)
            throws ServiceException;

    /**
     * 初始化第三方用户账号
     */
    public Account initialConnectAccount(String passportId, String ip, int provider)
            throws ServiceException;

    /**
     * 非第三方账号数据迁移，新写方法 初始化用户账号
     *
     * @param account
     * @return
     * @throws ServiceException
     */
    public boolean initAccount(Account account) throws ServiceException;


    /**
     * 根据passportId获取Account
     */
    public Account queryAccountByPassportId(String passportId) throws ServiceException;

    /**
     * 检测密码修改次数是否超出每天限制
     *
     * @param passportId
     * @return 不超出返回true，超出返回false
     * @throws ServiceException
     */
    public boolean checkLimitResetPwd(String passportId) throws ServiceException;

    /**
     * 验证账号的有效性，返回正常用户
     *
     * @return 验证不通过，则返回null
     */
    public Account queryNormalAccount(String passportId) throws ServiceException;

    /**
     * 验证用户名密码是否正确
     *
     * @return 用户名或密码不匹配，则返回null
     */
    public Result verifyUserPwdVaild(String passportId, String password, boolean needMD5) throws ServiceException;

    /**
     * 根据passportId删除Account表的缓存，增量数据迁移的内部debug接口使用
     */
    public boolean deleteAccountCacheByPassportId(String passportId) throws ServiceException;

    /**
     * 根据passportId删除Account表缓存和数据库
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException;

    /**
     * 重置密码
     */
    public boolean resetPassword(Account account, String password, boolean needMD5) throws ServiceException;

    /**
     * 根据ip看是否在黑名单中
     */
    public boolean isInAccountBlackListByIp(String passportId, String ip)
            throws ServiceException;

    /**
     * 激活验证邮件
     *
     * @return Result格式的返回值, 成功或失败，返回提示信息
     */
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip, String ru) throws ServiceException;

    /**
     * 激活验证邮件
     *
     * @return
     */
    public boolean activeEmail(String username, String token, int clientId) throws ServiceException;

    /**
     * 种根域和子域下的cookie
     *
     * @return
     */
    public boolean setCookie() throws Exception;

    /*
     *获取验证码
     */
    public Map<String, Object> getCaptchaCode(String code);

    /**
     * 校验验证码是否匹配
     *
     * @return 匹配结果
     */
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode);

    /**
     * 修改绑定手机
     * 只修改account表
     * @param account
     * @param newMobile
     * @return
     * @throws ServiceException
     */
    public boolean modifyMobileByAccount(Account account, String newMobile);

    /**
     * 首次绑定或修改绑定手机
     * 修改account和mobile_passportId_mapping
     *
     * @param account
     * @param newMobile
     * @return
     */
    public boolean bindOrModifyBindMobile(Account account, String newMobile) throws ServiceException;

    /**
     * 删除或解绑手机
     * 修改account和mobile_passportId_mapping
     * @param mobile
     * @return
     * @throws ServiceException
     */
    public boolean deleteOrUnbindMobile(String mobile) throws ServiceException;

    /**
     * 解禁或封禁用户
     *
     * @param account
     * @param newState
     * @return
     * @throws ServiceException
     */

    public boolean updateState(Account account, int newState) throws ServiceException;

    /*
     *检查验证码
     */
    public boolean checkCaptchaCode(String token, String captchaCode) throws ServiceException;

    /**
     * 更新昵称
     *
     * @param account
     * @param nickname
     * @return
     */
    public boolean updateUniqName(Account account, String nickname);

    /**
     * 更新头像
     *
     * @param account
     * @param avatar
     * @return
     */
    public boolean updateAvatar(Account account, String avatar);

    /**
     * 删除昵称
     *
     * @param nickname
     * @return
     * @throws ServiceException
     */
    public boolean removeUniqName(String nickname) throws ServiceException;

    /**
     * 检查昵称是否存在
     *
     * @param nickname
     * @return
     * @throws Exception
     */
    public String checkUniqName(String nickname) throws ServiceException;

    /*
    *获取激活信息
    */
    public Map<String, String> getActiveInfo(String username);

    /**
     * 根据passwordType验证用户密码是否正确
     *
     * @param password 用户需要验证的密码
     * @param account  用户实体类
     * @param needMD5  当passwordType=2时用到此参数
     * @return
     * @throws ServiceException
     */
    public Result verifyUserPwdValidByPasswordType(Account account, String password, Boolean needMD5) throws ServiceException;

}
