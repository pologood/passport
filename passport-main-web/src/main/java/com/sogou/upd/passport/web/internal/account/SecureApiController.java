package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.form.ModuleBlackListParams;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    //黑名单用户列表分隔符
    private static final String BLACK_USER_LIST_VALUE_SPLIT = "\r\n";

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
        StringBuffer resultText = new StringBuffer("0 0 10");
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

            //黑名单测试数据  nanajiaozixian1@sogou.com ~ nanajiaozixian99@sogou.com
            // TODO 暂写死 便于快速测试、之后改成从db中读取

            resultText.append(BLACK_USER_LIST_VALUE_SPLIT).append("nanajiaozixian1@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian2@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian3@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian4@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian5@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian6@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian7@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian8@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian9@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian10@sogou.com").append(BLACK_USER_LIST_VALUE_SPLIT);
            resultText.append("nanajiaozixian11@sogou.com");
            result.setSuccess(true);
            return resultText.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog("", String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(request));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
