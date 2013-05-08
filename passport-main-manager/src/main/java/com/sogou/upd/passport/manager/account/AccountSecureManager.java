package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.AccountSecureParams;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;

/**
 * 账户安全相关 User: mayan Date: 13-4-15 Time: 下午4:30 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountSecureManager {

    /**
     * 发送短信验证码
     */
    public Result sendMobileCode(String mobile, int clientId);

    /**
     * 发送短信验证码（根据passportId）
     */
    public Result sendMobileCodeByPassportId(String passportId, int clientId);

    /**
     * 重发验证码时更新缓存状态
     */
    public Result updateSmsCacheInfo(String cacheKey, int clientId);

    /**
     * 手机用户找回密码
     *
     * @param mobile   手机号码
     * @param clientId 客户端ID
     * @return Result格式的返回值，成功则发送验证码；失败，提示失败信息
     */
    public Result findPassword(String mobile, int clientId);

    /**
     * 手机用户重置密码
     *
     * @return Result格式的返回值, 成功或失败，返回提示信息
     */
    public Result resetPassword(MobileModifyPwdParams regParams) throws Exception;

    /**
     * 查询账户安全信息，包括邮箱、手机、密保问题，并模糊处理
     *
     * @param passportId
     * @return
     * @throws Exception
     */
    public Result queryAccountSecureInfo(String passportId) throws Exception;

    /**
     * 发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId) throws Exception;

    /**
     * 验证重置密码申请邮件
     *
     * @param uid 目前为passportId
     * @param token
     */
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception;

    /**
     * 重置用户密码（检查密保答案）
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param answer
     */
    public Result resetPasswordByQues(String passportId, int clientId, String password, String answer)
            throws Exception;

    /**
     * 重置用户密码（手机验证码方式）
     */
    public Result resetPasswordByMobile(String passportId, int clientId, String password, String smsCode) throws Exception;

    /**
     * 重置用户密码（邮件方式）---目前passportId与邮件申请链接中的uid一样
     */
    public Result resetPasswordByEmail(String passportId, int clientId, String password, String token) throws Exception;
}
