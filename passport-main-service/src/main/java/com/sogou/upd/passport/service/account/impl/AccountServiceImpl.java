package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.CaptchaUtils;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final String CACHE_PREFIX_PASSPORT_ACCOUNT = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT;
    private static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = CacheConstant.CACHE_PREFIX_PASSPORTID_IPBLACKLIST;
    private static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN;
    private static final String CACHE_PREFIX_UUID_CAPTCHA = CacheConstant.CACHE_PREFIX_UUID_CAPTCHA;
    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDNUM = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM;

    private static final String PASSPORT_ACTIVE_EMAIL_URL = "http://account.sogou.com/web/activemail?";


    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private CaptchaUtils captchaUtils;

    @Override
    public Account initialWebAccount(String username, String ip) throws ServiceException {
        Account account = null;
        String cacheKey = null;
        try {
            cacheKey = buildAccountKey(username);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                account = redisUtils.getObject(cacheKey, Account.class);
                if (account != null) {
                    account.setStatus(AccountStatusEnum.REGULAR.getValue());
                    long id = accountDAO.insertAccount(username, account);
                    if (id != 0) {
                        //删除临时账户缓存，成为正式账户
                        redisUtils.set(cacheKey, account);
                        //更新黑名单缓存
                        cacheKey = CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
                        redisUtils.increment(cacheKey);
                        //设置cookie
                        setCookie();
                        return account;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            //删除激活
            cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
            redisUtils.delete(cacheKey);
        }
        return null;
    }

    @Override
    public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider) throws ServiceException {
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, needMD5);
            }
            account.setPasswd(passwordSign);
            account.setRegTime(new Date());
            account.setRegIp(ip);
            account.setAccountType(provider);
            account.setStatus(AccountStatusEnum.REGULAR.getValue());
            account.setVersion(Account.NEW_ACCOUNT_VERSION);
            String mobile = null;
            if (AccountTypeEnum.isPhone(username, provider)) {
                mobile = username;
            }
            account.setMobile(mobile);
            long id = accountDAO.insertAccount(passportId, account);
            if (id != 0) {
                String cacheKey = buildAccountKey(passportId);
                redisUtils.set(cacheKey, account);
                return account;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String passportId, String ip, int provider) throws ServiceException {
        return initialAccount(passportId, null, false, ip, provider);
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        Account account;
        try {
            String cacheKey = buildAccountKey(passportId);

            account = redisUtils.getObject(cacheKey, Account.class);
            if (account == null) {
                account = accountDAO.getAccountByPassportId(passportId);
                if (account != null) {
                    redisUtils.set(cacheKey, account);
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return account;
    }

    @Override
    public Account verifyUserPwdVaild(String passportId, String password, boolean needMD5) throws ServiceException {

        Account userAccount;
        try {
            userAccount = queryAccountByPassportId(passportId);
        } catch (ServiceException e) {
            throw e;
        }
        try {
            if (userAccount != null && PwdGenerator.verify(password, needMD5, userAccount.getPasswd())) {
                return userAccount;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Account queryNormalAccount(String passportId) throws ServiceException {
        Account account = queryAccountByPassportId(passportId);
        if (account != null && account.isNormalAccount()) {
            return account;
        }
        return null;
    }

    @Override
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException {
        try {
            int row = accountDAO.deleteAccountByPassportId(passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean checkResetPwdLimited(String passportId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                              DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                int checkNum = Integer.parseInt(redisUtils.get(cacheKey));
                if (checkNum > DateAndNumTimesConstant.RESETNUM_LIMITED) {
                    // 当日验证码输入错误次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            String passwdSign = PwdGenerator.generatorStoredPwd(password, needMD5);
            int row = accountDAO.updatePassword(passwdSign, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setPasswd(passwdSign);
                redisUtils.set(cacheKey, account);

                // 设置密码修改次数限制
                String resetCacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                                       DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
                if (redisUtils.checkKeyIsExist(resetCacheKey)) {
                    redisUtils.increment(resetCacheKey);
                } else {
                    redisUtils.setWithinSeconds(resetCacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
                    /*redisUtils.set(resetCacheKey, "1");
                    redisUtils.expire(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);*/
                }
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean isInAccountBlackListByIp(String passportId, String ip) throws ServiceException {
        boolean flag = true;
        long ipCount = 0;
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
            String ipValue = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(ipValue)) {
                redisUtils.set(cacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY, TimeUnit.SECONDS);
            } else {
                ipCount = Long.parseLong(ipValue);
                //判断ip注册限制次数（一天20次）
                if (ipCount < DateAndNumTimesConstant.IP_LIMITED) {
                    redisUtils.increment(cacheKey);
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip) throws ServiceException {
        boolean flag = true;
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(username + clientId + code);
            String activeUrl =
                    PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
                            "&client_id=" + clientId +
                            "&token=" + token;

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            activeEmail.setMap(map);

            activeEmail.setTemplateFile("activemail.vm");
            activeEmail.setSubject("激活您的搜狗通行证帐户");
            activeEmail.setCategory("register");
            activeEmail.setToEmail(username);

            mailUtils.sendEmail(activeEmail);
            //连接失效时间
            String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
            redisUtils.setWithinSeconds(cacheKey, token, DateAndNumTimesConstant.TIME_TWODAY);
            /*redisUtils.set(cacheKey, token);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);*/
            //临时注册到缓存
            initialAccountToCache(username, passpord, ip);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean activeEmail(String username, String token, int clientId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                String tokenCache = redisUtils.get(cacheKey);
                if (tokenCache.equals(token)) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean setCookie() throws Exception {
//    ServletUtil.setCookie();
        return false;
    }

    @Override
    public Map<String, Object> getCaptchaCode(String token) throws ServiceException {
        Map<String, Object> map = null;
        try {
            if (Strings.isNullOrEmpty(token)) {
                token = UUID.randomUUID().toString().replaceAll("-", "");
            }
            String cacheKey = CACHE_PREFIX_UUID_CAPTCHA + token;
            //生成验证码
            map = captchaUtils.getRandcode();

            if (map != null && map.size() > 0) {

                String captchaCode = (String) map.get("captcha");
                map.put("token", token);

                redisUtils.setWithinSeconds(cacheKey, captchaCode, DateAndNumTimesConstant.CAPTCHA_INTERVAL);
                /*redisUtils.set(cacheKey, captchaCode);
                redisUtils.expire(cacheKey, DateAndNumTimesConstant.CAPTCHA_INTERVAL);*/
            } else {
                map = Maps.newHashMap();
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return map;
    }

    @Override
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_UUID_CAPTCHA + token;
            String captchaCodeCache = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(captchaCodeCache)) {
                if (captchaCodeCache.equalsIgnoreCase(captchaCode)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean modifyMobile(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateMobile(newMobile, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setMobile(newMobile);
                redisUtils.set(cacheKey, account);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean updateState(Account account, int newState) throws ServiceException {
       try {
           String passportId = account.getPassportId();
           int row = accountDAO.updateState(newState, passportId);
           if (row != 0) {
               String cacheKey = buildAccountKey(passportId);
               account.setStatus(newState);
               redisUtils.set(cacheKey, account);
               return true;
           }
       } catch (Exception e) {
           throw new ServiceException(e);
       }
       return false;
    }
  /*
   * 外域邮箱注册
   */
    public void initialAccountToCache(String username, String password, String ip) throws ServiceException {
        int provider = AccountTypeEnum.EMAIL.getValue();
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password,false);
            }
            account.setPasswd(passwordSign);
            account.setRegTime(new Date());
            account.setAccountType(provider);
            account.setStatus(AccountStatusEnum.DISABLED.getValue());
            account.setVersion(Account.NEW_ACCOUNT_VERSION);
            account.setRegIp(ip);

            String cacheKey = buildAccountKey(username);
            redisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);
            /*redisUtils.set(cacheKey, account);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);*/

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildAccountKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
  }



    /**
     * 根据登陆错误次数，判断是否需要在登陆时输入验证码
     *
     * @param passportId
     * @return
     */
    @Override
    public boolean loginFailedNumNeedCaptcha(String passportId) {
        int num = 0;
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_LOGINFAILEDNUM + passportId;
            String numStr = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(numStr)) {
                num = Integer.valueOf(numStr);
            }
            if (num >= LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT) {
                return true;
            }
        } catch (Exception e) {
            logger.error("getAccountLoginFailedCount:" + passportId, e);
        }
        return false;
    }

    @Override
    public long incLoginFailedNum(String passportId) {
        try{
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_LOGINFAILEDNUM + passportId;
            if(redisUtils.checkKeyIsExist(cacheKey)){
                return redisUtils.increment(cacheKey);
            }else{
                redisUtils.set(cacheKey,1);
            }
        } catch (Exception e) {
            logger.error("incLoginFailedNum:" + passportId, e);
        }
        return 1;
    }

    @Override
    public boolean clearLoginFailedNum(String passportId) {
        try{
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_LOGINFAILEDNUM + passportId;
            redisUtils.delete(cacheKey);
            return true;
        }catch (Exception e) {
            logger.error("clearLoginFailedNum:" + passportId, e);
        }
        return false;
    }
}
