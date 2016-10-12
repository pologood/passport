package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.ModuleBlackListParams;
import com.sogou.upd.passport.manager.api.account.form.SendSmsApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdatePswParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import static com.sogou.upd.passport.common.parameter.AccountDomainEnum.THIRD;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/security")
public class SecureApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SecureApiController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private RegisterApiManager registerApiManager;

    @Autowired
    private RedisUtils redisUtils;

    //黑名单用户列表分隔符
    private static final String BLACK_USER_LIST_VALUE_SPLIT = "\r\n";

    //返回给module结果集中userid与时间戳的分隔符
    private static final String BLACK_USER_EXPIRETIME_SPLIT = " ";

    //redis中保存黑名单userid与时间戳分隔符
    private static final String BLACK_USER_EXPIRETIME_REDIS_SPLIT = "\\^";

    //有效期
    private static final int EXPIRE_TIME = 60;

    /**
     * 手机发送短信重置密码
     */
    @RequestMapping(value = "/resetpwd_batch", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String resetpwd(HttpServletRequest request, BaseResetPwdApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String lists = params.getLists();
        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, request)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            if (!Strings.isNullOrEmpty(lists)) {
                List<UserNamePwdMappingParams> list = new ObjectMapper().readValue(lists, new TypeReference<List<UserNamePwdMappingParams>>() {
                });
                result = secureManager.resetPwd(list, clientId);
            } else {
                result.setSuccess(true);
                result.setMessage("lists为空");
            }
            return result.toString();
        } catch (Exception e) {
            log.error("Batch resetpwd fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("lists", lists);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    @RequestMapping(value = "/sendsms", method = RequestMethod.GET)
    @ResponseBody
    @InterfaceSecurity
    public Object sendSmsNewMobile(HttpServletRequest request, SendSmsApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String sgid = params.getSgid();
        String mobile = params.getMobile();
        String ip = params.getCreateip();
        String passportId = "UNKNOWN";
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            result = sessionServerManager.getPassportIdBySgid(sgid, ip);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }

            passportId = (String)result.getModels().get("passport_id");

            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (domain == AccountDomainEnum.PHONE) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result;
            }

            if (domain == AccountDomainEnum.THIRD) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result;
            }
            //双读，检查新手机是否允许绑定
            result = registerApiManager.checkUser(mobile, clientId, false);
            if (!result.isSuccess()) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("手机号已绑定其他账号");
                return result.toString();
            }
            result = secureManager.sendMobileCode(mobile, clientId, AccountModuleEnum.SECURE);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("mobile", mobile);
            userOperationLog.putOtherMessage("sgid", sgid);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /**
     * 绑定密保手机
     */
    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String bindmobile(HttpServletRequest request, BindMobileApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String sgid = params.getSgid();
        String mobile = params.getMobile();
        String ip = params.getCreateip();
        String passportId = "UNKNOWN";
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            result = sessionServerManager.getPassportIdBySgid(sgid, ip);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }

            passportId = (String)result.getModels().get("passport_id");

            result = secureManager.bindMobileByPassportId(passportId, clientId, mobile, params.getSmscode(), params.getPassword(), false, ip);
            return result.toString();
        } catch (Exception e) {
            log.error("bind mobile fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("mobile", mobile);
            userOperationLog.putOtherMessage("sgid", sgid);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /**
     * module黑名单接口
     * <p/>
     * 数据格式 数据全量(增量)，获取增量数据的偏移标志位  黑名单接口调用间隔(单位秒)
     *
     * @param request
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/moduleblacklist", method = RequestMethod.GET)
    @ResponseBody
    public String moduleBlackList(HttpServletRequest request, ModuleBlackListParams params) throws Exception {
        Result result = new APIResultSupport(false);

        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                return result.toString();
            }
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }


            //有效期 （当前时间+60秒）秒
//            long expireTime = (System.currentTimeMillis() / 1000) + EXPIRE_TIME;

            //获取Redis中保存的黑名单数据
            Set<String> set = redisUtils.smember(CacheConstant.CACHE_KEY_BLACKLIST);

//            if (params.getIs_delta() != 0 || params.getIs_delta() != 1) {
//                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//                return result.toString();
//            }

            //接口调用频率
//            int update_internal;
//            if (params.getUpdate_interval() == 0) {
//                update_internal = 10;
//            } else {
//                update_internal = params.getUpdate_interval();
//            }

            //  module 限制 update_internal 最小调用频次为 10s
            StringBuffer resultText = new StringBuffer("0 0 10");
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String str = it.next();
                String strs[] = str.split(BLACK_USER_EXPIRETIME_REDIS_SPLIT);
                resultText.append(BLACK_USER_LIST_VALUE_SPLIT).append(strs[0]).append(BLACK_USER_EXPIRETIME_SPLIT).append(strs[1]);
            }

            result.setSuccess(true);
            return resultText.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog("", String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(request));
            UserOperationLogUtil.log(userOperationLog);
        }
    }
    
    /**
     * 修改密码
     *
     * @param updateParams 传入的参数
     */
    @InterfaceSecurity
    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePwd(HttpServletRequest request, UpdatePswParams updateParams)
      throws Exception {
        Result result = new APIResultSupport(false);
    
        // 参数校验
        String validateResult = ControllerHelper.validateParams(updateParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
    
        String passportId = updateParams.getUserid();
        String clientId = String.valueOf(updateParams.getClient_id());
        String password = updateParams.getPassword();
        String newPwd = updateParams.getNewpwd();
        String ip = updateParams.getIp();
        
        try {
            if(AccountDomainEnum.THIRD.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTALLOWED);
                    return result.toString();
            }
    
            UpdatePwdParameters updatePwdParameters = new UpdatePwdParameters();
            updatePwdParameters.setClient_id(clientId);
            updatePwdParameters.setPassport_id(passportId);
            updatePwdParameters.setPassword(password);
            updatePwdParameters.setNewpwd(newPwd);
            updatePwdParameters.setIp(ip);
            
            result = secureManager.updateWebPwd(updatePwdParameters);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), clientId, result.getCode(), ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
