package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public interface CommonManager {

    /**
     * 检验code是否正确
     *
     * @param firstStr
     * @param clientId
     * @param ct
     * @param originalCode
     * @return
     */
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode);

    /**
     * 根据字符串获取code值
     *
     * @param firstStr
     * @param clientId
     * @param ct
     * @return
     */
    public String getCode(String firstStr, int clientId, long ct);

    /**
     * 根据用户输入的username获取主账号的passportId
     * 1.输入：手机号；输出：主账号userid，注意：如果手机号未注册或未绑定，则返回null
     * 2.输入：手机号@sohu.com；输出：手机号@sohu.com
     * 3.输入：个性名；输出：个性名@sogou.com
     * 4.输入：搜狐域、外域邮箱账号；输出：保持不变
     *
     * @param username
     * @return
     */
    public String getPassportIdByUsername(String username) throws Exception;

    /**
     * 获取安全码
     *
     * @param passportId
     * @param clientId
     * @param cacheKey
     * @return
     * @throws ServiceException
     */
    public String getSecureCode(String passportId, int clientId, String cacheKey) throws ServiceException;

    /**
     * 校验安全码
     *
     * @param passportId
     * @param clientId
     * @param secureCode
     * @param cacheKey
     * @return
     * @throws ServiceException
     */
    public boolean checkSecureCode(String passportId, int clientId, String secureCode, String cacheKey) throws ServiceException;

    /**
     * 根据passportId查询account
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public Account queryAccountByPassportId(String passportId) throws ServiceException;

    /**
     * 根据passportId 获取Account
     * 1. 只查询Redies中账号信息
     * 2. 用于账号注册激活邮件重新发送
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public Account queryAccountByPassportIdInCache(String passportId) throws ServiceException;

    /**
     * 应用是否有此API访问权限
     * 1.应用服务器在appconfig配置里的server_ip白名单里；
     * 2.APIName在appconfig配置里的scope里；
     *
     * @param clientId
     * @param requestIp 服务器ip
     * @param apiName   如果此API有访问限制则传API名称，否则传null
     * @return
     */
    public boolean isAccessAccept(int clientId, String requestIp, String apiName);

    /**
     * 检查手机注册ip是否在发短信超限黑名单中
     *
     * @param ipOrMobile
     * @param client_id
     * @return
     * @throws Exception
     */
    public Result checkMobileSendSMSInBlackList(String ipOrMobile, String client_id) throws Exception;

    /**
     * 手机发短信次数
     *
     * @param ipOrMobile
     * @throws Exception
     */
    public void incSendTimesForMobile(String ipOrMobile) throws Exception;

}
