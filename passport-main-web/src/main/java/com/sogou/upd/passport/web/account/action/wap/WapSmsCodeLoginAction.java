package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.SSOScanAccountType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.SmsCodeLoginManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.manager.form.WapSmsCodeLoginParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import com.sogou.upd.passport.web.account.form.WapIndexParams;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录
 * User: chengang
 * Date: 15-6-8
 * Time: 下午3:36
 */
@Controller
@RequestMapping(value = "/wap")
public class WapSmsCodeLoginAction extends WapV2BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WapSmsCodeLoginAction.class);

    @Autowired
    private SmsCodeLoginManager smsCodeLoginManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    @RequestMapping(value = "/smsCodeLogin/index")
    public String smsCodeLoginIndex(HttpServletRequest request, HttpServletResponse response, Model model, WapIndexParams wapIndexParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(wapIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            String ru = wapIndexParams.getRu();
            if (validateResult.contains("域名不正确")) {  // TODO 最好是在RuValidator统一修改
                ru = CommonConstant.DEFAULT_WAP_URL;
            }
            response.sendRedirect(getIndexErrorReturnStr(ru, result.getMessage()));
            return "empty";
        }

        model.addAttribute("v", wapIndexParams.getV());
        model.addAttribute("ru", wapIndexParams.getRu());
        model.addAttribute("client_id", wapIndexParams.getClient_id());
        model.addAttribute("errorMsg", wapIndexParams.getErrorMsg());
        model.addAttribute("isNeedCaptcha", wapIndexParams.getNeedCaptcha());
        model.addAttribute("skin", wapIndexParams.getSkin());
        //生成token
        String token = RandomStringUtils.randomAlphanumeric(48);
        model.addAttribute("token", token);

        if (WapConstant.WAP_SIMPLE.equals(wapIndexParams.getV())) {
            response.setHeader("Content-Type", "text/vnd.wap.wml;charset=utf-8");
            return "wap/index_simple";
        } else if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            return "wap/index_smscode_login_touch";
        } else {
            if (!Strings.isNullOrEmpty(wapIndexParams.getErrorMsg())) {
                model.addAttribute("hasError", true);
            } else {
                model.addAttribute("hasError", false);
            }
            model.addAttribute("ru", Strings.isNullOrEmpty(wapIndexParams.getRu()) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(wapIndexParams.getRu()));
            if (wapIndexParams.getNeedCaptcha() == 1) {
                model.addAttribute("isNeedCaptcha", 1);
                model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
            }
            model.addAttribute("mobile", wapIndexParams.getMobile());
            return "wap/login_wap";
        }

    }

    @RequestMapping(value = "/smsCode/login", method = RequestMethod.POST)
    public String smsCodeLogin(HttpServletRequest request, HttpServletResponse response, WapSmsCodeLoginParams loginParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        try {
            if (null != loginParams.getRu()) {
                loginParams.setRu(Coder.decodeUTF8(loginParams.getRu()));
            }

            //参数验证
            String validateResult = ControllerHelper.validateParams(loginParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return getErrorReturnStr(loginParams, validateResult, 0);
            }

            //登录验证
            result = smsCodeLoginManager.smsCodeLogin(loginParams, ip);  //wap端ip做安全限制

            //如果校验用户名和短信验证码成功，则生成登录态sgid
            if (result.isSuccess()) {
                //在返回的数据中导入 json格式，用来给客户端用。
    
                String userId = (String) result.getModels().get(CommonConstant.USERID);
                String sgid = (String) result.getModels().get(LoginConstant.COOKIE_SGID);
                WapRegAction.setSgidCookie(response, sgid);
    
                //第三方获取个人资料
                String fields = "uniqname,avatarurl,gender";
                ObtainAccountInfoParams
                        accountInfoParams = new ObtainAccountInfoParams(loginParams.getClient_id(), userId, fields);
                result = accountInfoManager.getUserInfo(accountInfoParams);
                
                result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                
                String ssoScanAcountType= SSOScanAccountType.getSSOScanAccountType(userId);
                result.getModels().put(LoginConstant.SSO_ACCOUNT_TYPE,ssoScanAcountType);
    
                processAvatarUrl(request, result);
                
                writeResultToResponse(response, result);
                smsCodeLoginManager.doAfterLoginSuccess(loginParams.getMobile(), ip, userId, Integer.parseInt(loginParams.getClient_id()));
                return "empty";
            } else {
                //如果校验用户名和密码失败，且是因为需要验证码，则置验证码为1，即需要验证码
                int isNeedCaptcha = 0;
                smsCodeLoginManager.doAfterLoginFailed(loginParams.getMobile(), ip, result.getCode());
                //校验是否需要验证码
                if (result.getCode() == ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE) {
                    isNeedCaptcha = 1;
                    if (WapConstant.WAP_JSON.equals(loginParams.getV())) {
                        writeResultToResponse(response, result);
                        return "empty";
                    }
                    return getErrorReturnStr(loginParams, result.getMessage(), isNeedCaptcha);
                }
                //否则，还需要校验是否需要弹出验证码
                boolean needCaptcha = smsCodeLoginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getMobile(), getIp(request));
                if (needCaptcha) {
                    isNeedCaptcha = 1;
                }
                String defaultMessage = result.getMessage();
                //不直接返回直接的文案告诉用户中了安全限制
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                    result.setMessage("您登陆过于频繁，请稍后再试。");
                    defaultMessage = "您登陆过于频繁，请稍后再试。";
                }
                if (WapConstant.WAP_JSON.equals(loginParams.getV())) {
                    writeResultToResponse(response, result);
                    return "empty";
                }
                return getErrorReturnStr(loginParams, defaultMessage, isNeedCaptcha);
            }
        } catch (Exception e) {
            LOGGER.error("smsCodeLogin error,message:{}", e.getMessage());
        } finally {
            //用户登录log
            UserOperationLog userOperationLog = new UserOperationLog(loginParams.getMobile(), request.getRequestURI(), loginParams.getClient_id(), result.getCode(), ip);
            String refer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", refer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return "empty";
    }


    @RequestMapping(value = "/smsCodeLogin/sendSms", method = RequestMethod.POST)
    @ResponseBody
    public Object sendSmsCode(HttpServletRequest request, HttpServletResponse response, MoblieCodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int client_id = Integer.parseInt(params.getClient_id());
            result = smsCodeLoginManager.sendSmsCode(params.getMobile(), client_id, params.getToken(), params.getCaptcha());
            model.addAttribute("client_id", params.getClient_id());
        } catch (Exception e) {
            logger.error("sendSmsCode error,mobile is " + params.getMobile(), e);
        } finally {
            //用户登录log
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), result.getCode(), getIp(request));
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();


    }


    private String getErrorReturnStr(WapSmsCodeLoginParams loginParams, String errorMsg, int isNeedCaptcha) {
        StringBuilder returnStr = new StringBuilder();
        returnStr.append("redirect:/wap/smsCodeLogin/index?");
        if (!Strings.isNullOrEmpty(loginParams.getV())) {
            returnStr.append("v=" + loginParams.getV());
        }
        if (!Strings.isNullOrEmpty(loginParams.getRu())) {
            if (WapConstant.WAP_COLOR.equals(loginParams.getV())) {
                returnStr.append("&ru=" + Coder.encodeUTF8(loginParams.getRu()));
            } else {
                returnStr.append("&ru=" + loginParams.getRu());
            }
        }
        if (!Strings.isNullOrEmpty(loginParams.getClient_id())) {
            returnStr.append("&client_id=" + loginParams.getClient_id());
        }
        if (!Strings.isNullOrEmpty(loginParams.getSkin())) {
            returnStr.append("&skin=" + loginParams.getSkin());
        }
        if (!Strings.isNullOrEmpty(errorMsg)) {
            returnStr.append("&errorMsg=" + Coder.encodeUTF8(errorMsg));
        }
        returnStr.append("&needCaptcha=" + isNeedCaptcha);
        if (WapConstant.WAP_COLOR.equals(loginParams.getV())) {
            returnStr.append("&mobile=" + loginParams.getMobile());
        }
        return returnStr.toString();
    }


    private String getIndexErrorReturnStr(String ru, String errorMsg) {
        if (!Strings.isNullOrEmpty(ru)) {
            return (ru + "?errorMsg=" + Coder.encodeUTF8(errorMsg));
        }
        return WapConstant.WAP_INDEX + "?errorMsg=" + Coder.encodeUTF8(errorMsg);
    }


    private void writeResultToResponse(HttpServletResponse response, Result result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result.toString());
    }

}
