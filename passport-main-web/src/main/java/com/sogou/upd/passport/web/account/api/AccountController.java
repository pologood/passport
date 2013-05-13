package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.MobileRegParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.manager.form.MoblieCodeParams;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 移动用户注册登录 User: mayan Date: 13-3-22 Time: 下午3:29 To change this template use File | Settings |
 * File Templates.
 */
@Controller
@RequestMapping("/v2")
public class AccountController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountSecureManager accountSecureManager;
    @Autowired
    private AccountRegManager accountRegManager;
    @Autowired
    private AccountManager accountManager;
    @Autowired
    private ConfigureManager configureManager;

    /**
     * 手机账号获取，重发手机验证码接口
     *
     * @param reqParams 传入的参数
     */
    @RequestMapping(value = "/sendmobilecode", method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(MoblieCodeParams reqParams)
            throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        //手机号校验
      String mobile = reqParams.getMobile();
        if (!PhoneUtil.verifyPhoneNumberFormat(mobile)) {
          return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
        }
        //验证client_id
        int clientId;
        try {
            clientId = Integer.parseInt(reqParams.getClient_id());
        } catch (NumberFormatException e) {
            return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }

        Result result = accountSecureManager.sendMobileCode(mobile, clientId);
        return result;

    }

    /**
     * 手机账号正式注册调用
     */
    @RequestMapping(value = "/mobile/reg", method = RequestMethod.POST)
    @ResponseBody
    public Object mobileUserRegister(HttpServletRequest request, MobileRegParams regParams) {
        // 请求参数校验，必填参数是否正确，手机号码格式是否正确
        //参数验证
        String validateResult = ControllerHelper.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        int clientId;
        try {
            clientId = Integer.parseInt(regParams.getClient_id());
        } catch (NumberFormatException e) {
            return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }
        String ip = getIp(request);
        String mobile = regParams.getMobile();
        try {
            if (accountManager.isAccountExists(mobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            }
        } catch (Exception e) {
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }

        Result result = accountRegManager.mobileRegister(regParams, ip);
        return result;
    }

    /**
     * 找回用户密码
     */
    @RequestMapping(value = "/findpwd", method = RequestMethod.GET)
    @ResponseBody
    public Object findPassword(MoblieCodeParams reqParams)
            throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        int clientId;
        try {
            clientId = Integer.parseInt(reqParams.getClient_id());
        } catch (NumberFormatException e) {
            return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }

        String mobile = reqParams.getMobile();
        try {
            if (!accountManager.isAccountExists(mobile)) {
                return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
            }
        } catch (Exception e) {
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        Result result = accountSecureManager.findPassword(reqParams.getMobile(), clientId);
        return result;
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "/mobile/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPassword(MobileModifyPwdParams regParams) throws Exception {

        String validateResult = ControllerHelper.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        //验证client_id
        int clientId;
        try {
            clientId = Integer.parseInt(regParams.getClient_id());
        } catch (NumberFormatException e) {
            return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }

        Result result = accountSecureManager.resetPassword(regParams);
        return result;
    }

}
