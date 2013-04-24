package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.Account;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountService {

    /**
     * 初始化非第三方用户账号
     */
    public Account initialAccount(String username, String password, String ip, int provider)
            throws ServiceException;

    /**
     * 初始化第三方用户账号
     */
    public Account initialConnectAccount(String passportId, String ip, int provider)
            throws ServiceException;

    /**
     * 根据passportId获取Account
     */
    public Account queryAccountByPassportId(String passportId) throws ServiceException;

    /**
     * 验证账号的有效性，是否为正常用户
     *
     * @return 验证不通过，则返回null
     */
    public Account verifyAccountVaild(String passportId) throws ServiceException;

    /**
     * 验证用户名密码是否正确
     *
     * @return 用户名或密码不匹配，则返回null
     */
    public Account verifyUserPwdVaild(String passportId, String password) throws ServiceException;

    /**
     * 根据passportId删除Account，内部debug接口使用
     */
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException;

    /**
     * 重置密码
     */
    public Account resetPassword(String passportId, String password) throws ServiceException;
}
