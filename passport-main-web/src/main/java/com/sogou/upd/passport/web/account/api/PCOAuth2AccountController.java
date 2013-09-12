package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.OAuth2AuthorizeManager;
import com.sogou.upd.passport.manager.account.PCOAuth2RegManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.PCOAuth2IndexParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2UpdateNickParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * sohu+浏览器相关接口替换
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-9-9
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/oauth2")
public class PCOAuth2AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PCOAuth2AccountController.class);
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManagerImpl;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @RequestMapping(value = "/pclogin", method = RequestMethod.GET)
    public String pcLogin(Model model) throws Exception {
        return "/oauth2pc/pclogin";
    }

    @Autowired
    private RegManager regManager;
    @Autowired
    private PCOAuth2RegManager pcoAuth2RegManager;

    /**
     * 浏览器sohu+注册接口
     *
     * @param request
     * @param pcoAuth2RegisterParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(HttpServletRequest request, PCOAuth2RegisterParams pcoAuth2RegisterParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcoAuth2RegisterParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String ip = getIp(request);
        //TODO ip加安全限制
        result = checkAccountNotExists(pcoAuth2RegisterParams.getUsername());
        if (!result.isSuccess()) {
            return result.toString();
        }
//        result = regManager.webRegister(pcoAuth2RegisterParams, ip);
        //注册成功后获取token
        if (result.isSuccess()) {
            PcPairTokenParams pcPairTokenParams = new PcPairTokenParams();
            pcPairTokenParams.setAppid(pcoAuth2RegisterParams.getClient_id());
            pcPairTokenParams.setUserid(pcoAuth2RegisterParams.getUsername());
            pcPairTokenParams.setTs(pcoAuth2RegisterParams.getInstance_id());
            result = pcoAuth2RegManager.getPairToken(pcPairTokenParams);
            if (result.isSuccess()) {
                AccountToken accountToken = (AccountToken) result.getDefaultModel();
                // 获取昵称并返回
                String passportId = accountToken.getPassportId();
                GetUserInfoApiparams userInfoApiParams = new GetUserInfoApiparams(passportId, "uniqname");
                Result userInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(userInfoApiParams);
                String uniqname;
                if (userInfoResult.isSuccess()) {
                    uniqname = (String) userInfoResult.getModels().get("uniqname");
                    uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
                } else {
                    uniqname = defaultUniqname(passportId);
                }
                result.setDefaultModel("uniqname", uniqname);
            }
        }
        return result.toString();
    }

    //检查用户是否存在
    private Result checkAccountNotExists(String username) throws Exception {
        Result result = new APIResultSupport(false);
        //不允许sohu域用户注册
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
            return result;
        }
        //校验是搜狗用户还是手机用户
        if (AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(username))) {
            //sogou用户
            result = regManager.isAccountNotExists(username, false);
        } else {
            //手机用户
            result = regManager.isAccountNotExists(username, true);
        }
        return result;
    }


    @RequestMapping(value = "/pcindex", method = RequestMethod.GET)
    public String pcindex(HttpServletRequest request, HttpServletResponse response,PCOAuth2IndexParams oauth2PcIndexParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //TODO 校验token,获取userid
        String passportId = "tinkame700@sogou.com";

        //获取昵称
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
        Result getUserInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
        String uniqname;
        if (getUserInfoResult.isSuccess()) {
            uniqname = (String) getUserInfoResult.getModels().get("uniqname");
            uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
        } else {
            uniqname = defaultUniqname(passportId);
        }
        model.addAttribute("userid", passportId);
        model.addAttribute("uniqname", uniqname);

        //判断账号类型,判断绑定手机或者绑定邮箱是否可用
        AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(passportId);
        switch (accountDomain) {
            case SOHU:
                model.addAttribute("isBindEmailUsable", 0);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            case THIRD:
                model.addAttribute("isBindEmailUsable", 0);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            case PHONE:
                model.addAttribute("isBindEmailUsable", 1);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            default:
                model.addAttribute("isBindEmailUsable", 1);
                model.addAttribute("isBindMobileUsable", 1);
                break;
        }
        //获取绑定手机，绑定邮箱
        result = secureManager.queryAccountSecureInfo(passportId, CommonConstant.PC_CLIENTID, true);
        if(result.isSuccess()){
            if(result.getModels().get("sec_mobile")!=null){
                model.addAttribute("bindMoblile", result.getModels().get("sec_mobile"));
            }else {
                model.addAttribute("bindMoblile","");
            }

            if(result.getModels().get("sec_email")!=null){
                model.addAttribute("bindEmail",result.getModels().get("sec_email"));
            } else {
                model.addAttribute("bindEmail","");
            }
        }

        //生成cookie
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(passportId);
        createCookieUrlApiParams.setRu("https://account.sogou.com");
        //TODO sogou域账号迁移后cookie生成问题
        Result getCookieValueResult = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        if (getCookieValueResult.isSuccess()) {
            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
            String passport = (String) getCookieValueResult.getModels().get("passport");
            ServletUtil.setCookie(response, "ppinf", ppinf, 0, CommonConstant.SOHU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, 0, CommonConstant.SOHU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "passport", passport, 0, CommonConstant.SOHU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
        }
        return "/oauth2pc/pcindex";
    }

    @RequestMapping(value = "/checknickname", method = RequestMethod.GET)
    @ResponseBody
    public Object checkNickName(HttpServletRequest request,@RequestParam(value = "nickname") String nickname){
        Result result = new APIResultSupport(false);
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams=new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(nickname);
        updateUserUniqnameApiParams.setClient_id(SHPPUrlConstant.APP_ID);
        result = proxyUserInfoApiManagerImpl.checkUniqName(updateUserUniqnameApiParams);
        return result.toString();
    }

    @RequestMapping(value = "/updateNickName",method = RequestMethod.POST)
    @ResponseBody
    public Object updateNickName(HttpServletRequest request,PCOAuth2UpdateNickParams pcOAuth2UpdateNickParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcOAuth2UpdateNickParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //TODO 校验token,获取userid
        String userid="tinkame700@sogou.com";

        UpdateUserInfoApiParams params = new UpdateUserInfoApiParams();
        params.setUserid(userid);
        params.setModifyip(getIp(request));
        params.setUniqname(pcOAuth2UpdateNickParams.getNick());
        result = proxyUserInfoApiManagerImpl.updateUserInfo(params);
        return result.toString();
    }



    @RequestMapping(value = "/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;

    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/token")
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenASRequest oauthRequest;
        Result result = new APIResultSupport(false);
        try {
            oauthRequest = new OAuthTokenASRequest(request);
        } catch (OAuthProblemException e) {
            result.setCode(e.getError());
            result.setMessage(e.getDescription());
            return result.toString();
        }

        int clientId = oauthRequest.getClientId();

        // 检查client_id和client_secret是否有效
        AppConfig appConfig = configureManager.verifyClientVaild(clientId, oauthRequest.getClientSecret());
        if (appConfig == null) {
            result.setCode(ErrorUtil.INVALID_CLIENT);
            return result.toString();
        }
        result = oAuth2AuthorizeManager.oauth2Authorize(oauthRequest,appConfig);

        return result.toString();
    }
}