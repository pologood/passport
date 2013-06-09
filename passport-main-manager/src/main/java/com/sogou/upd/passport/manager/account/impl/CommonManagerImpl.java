package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:36
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CommonManagerImpl implements CommonManager {

    private static Logger log = LoggerFactory.getLogger(CommonManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Override
    public boolean isAccountExists(String username) throws Exception {
        try {
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (!Strings.isNullOrEmpty(passportId)) {
                    return true;
                }
            } else {
                Account account = accountService.queryAccountByPassportId(username);
                if (account != null) {
                    return true;
                }
            }
        } catch (ServiceException e) {
            log.error("Check account is exists Exception, username:" + username, e);
            throw new Exception(e);
        }
        return false;
    }

    @Override
    public String getPassportIdByUsername(String username) throws Exception {
        try {
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (!Strings.isNullOrEmpty(passportId)) {
                    return passportId;
                }
            } else {
                // Account account = accountService.queryAccountByPassportId(username);
                // 不查询account表
                return username;
            }
        } catch (ServiceException e) {
            log.error("Username doesn't exist Exception, username:" + username, e);
            throw new Exception(e);
        }
        return null;
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws Exception {
        return  accountService.queryAccountByPassportId(passportId);
    }

    @Override
    public boolean updateState(Account account, int newState)  throws Exception{
      return  accountService.updateState(account,newState);
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws Exception {
      return  accountService.resetPassword(account,password,needMD5);
    }
}