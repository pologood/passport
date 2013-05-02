package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.form.MoblieCodeParams;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.web.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: mayan Date: 13-4-28 Time: 下午4:07 To change this template use File | Settings | File
 * Templates.
 */
@Controller
@RequestMapping("/web")
public class AccountAction extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(AccountAction.class);

  @Autowired
  private AccountRegManager accountRegManager;
  @Autowired
  private AccountManager accountManager;

  /**
   * 手机账号获取，重发手机验证码接口
   *
   * @param regParams 传入的参数
   */
  @RequestMapping(value = "/reguser", method = RequestMethod.POST)
  @ResponseBody
  public Object reguser(WebRegisterParameters regParams)
      throws Exception {
    int clientId = regParams.getClient_id();
    String username = regParams.getUsername();
    String password = regParams.getPassword();
    String code = regParams.getCode();
    //todo 参数验证

    //todo 验证码校验

    Result result = new Result();
    //验证用户是否注册过
    if (!accountManager.isAccountExists(username)) {

      accountRegManager.webRegister(regParams);


    } else {
      result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
    }
    return result;
  }

}
