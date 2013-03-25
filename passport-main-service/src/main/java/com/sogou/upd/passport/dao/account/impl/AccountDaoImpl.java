package com.sogou.upd.passport.dao.account.impl;

import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-22
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AccountDaoImpl implements AccountDao {

    @Inject
    public AccountMapper accountMapper;

    @Override
    public boolean findUserRegisterIsOrNot(Account account) {
        return accountMapper.findUserRegisterIsOrNot(account);
    }

    @Override
    public void userRegister(Account account) {
        accountMapper.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
