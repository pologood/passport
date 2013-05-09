package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Map;

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
    @Autowired
    private ConfigureManager configureManager;

    /*
       web注册页跳转
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> mapResult = accountRegManager.getCaptchaCode(null);
        if (mapResult != null && mapResult.size() > 0) {
            ImageIO.write((BufferedImage) mapResult.get("image"), "JPEG", response.getOutputStream());//将内存中的图片通过流动形式输出到客户端

            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
        }

        //todo 渲染到页面

        return null;
    }

    /**
     * web页面注册
     *
     * @param regParams 传入的参数
     */
    @RequestMapping(value = "/reguser", method = RequestMethod.POST)
    @ResponseBody
    public Object reguser(HttpServletRequest request, WebRegisterParameters regParams)
            throws Exception {
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

        String username = regParams.getUsername();
        String password = regParams.getPassword();
        String code = regParams.getCode();

        //todo 验证码校验

        //检查client_id格式以及client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }

        Result result = null;
        //验证用户是否注册过
        if (!accountManager.isAccountExists(username)) {
            String ip = getIp(request);
            result = accountRegManager.webRegister(regParams, ip);
        } else {
            result = result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
        }
        return result;
    }

    /**
     * 邮件激活
     *
     * @param activeParams 传入的参数
     */
    @RequestMapping(value = "/activemail", method = RequestMethod.GET)
    @ResponseBody
    public Object activeEmail(HttpServletRequest request, ActiveEmailParameters activeParams)
            throws Exception {

        //参数验证
        String validateResult = ControllerHelper.validateParams(activeParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        int clientId;
        try {
            clientId = Integer.parseInt(activeParams.getClient_id());
        } catch (NumberFormatException e) {
            return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return Result.buildError(ErrorUtil.INVALID_CLIENTID);
        }

        //邮件激活
        Result result = accountRegManager.activeEmail(activeParams);

        return result;
    }
}
