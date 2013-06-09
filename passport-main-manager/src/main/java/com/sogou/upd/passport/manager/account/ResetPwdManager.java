package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-8 Time: 上午10:46 To change this template use
 * File | Settings | File Templates.
 */
public interface ResetPwdManager {
    /**
     * 修改密码，包括检查修改次数
     *
     * @param passportId
     * @param clientId
     * @param password
     * @return
     * @throws Exception
     */
    public Result resetPassword(String passportId, int clientId, String password) throws Exception;

    /* ------------------------------------重置密码Begin------------------------------------ */

    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @param useRegEmail 邮件选择方式，true为注册邮箱，false为绑定邮箱
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail) throws Exception;


    /**
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     *
     * @param uid 目前为passportId
     * @param token
     */
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception;

    /**
     * 重置密码（邮件方式）——3.再一次验证token，并修改密码。目前passportId与邮件申请链接中的uid一样
     */
    public Result resetPasswordByEmail(String passportId, int clientId, String password, String token)
            throws Exception;


    /**
     * 重置密码（手机方式）——2.检查手机短信码，成功则返回secureCode记录成功标志
     *                      （1.发送见sendMobileCodeByPassportId）
     *
     * @param passportId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeResetPwd(String passportId, int clientId, String smsCode) throws Exception;

    /**
     * 重置密码（密保方式）——1.验证密保答案及captcha，成功则返回secureCode记录成功标志。(可用于其他功能模块)
     *
     * @param passportId
     * @param clientId
     * @param answer
     * @param token
     * @param captcha
     * @return
     * @throws Exception
     */
    public Result checkAnswerByPassportId(String passportId, int clientId, String answer, String token,
                                          String captcha) throws Exception;

    /**
     * 重置密码（手机和密保方式）——根据secureCode修改密码（secureCode由上一步验证手机或密保问题成功获取）
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param secureCode
     * @return
     * @throws Exception
     */
    public Result resetPasswordByScode(String passportId, int clientId, String password,
                                       String secureCode) throws Exception;

    /**
     * 重置用户密码（检查密保答案）——暂不用！！！
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param answer
     */
    public Result resetPasswordByQues(String passportId, int clientId, String password, String answer)
            throws Exception;

    /**
     * 重置用户密码（手机验证码方式）——暂不用！！！
     */
    public Result resetPasswordByMobile(String passportId, int clientId, String password, String smsCode)
            throws Exception;

    /* ------------------------------------重置密码End------------------------------------ */
}