package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.manager.api.account.BaseRegUserApiParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * 邮箱、个性域名注册参数类
 * User: shipengzhi
 * Date: 13-6-8
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class RegEmailApiParams extends BaseRegUserApiParams {
    @NotBlank(message = "密码不允许为空")
    private String password;  //明文密码，需要对格式校验
    @NotBlank(message = "注册IP不允许为空")
    private String createip;  //注册IP
    private String send_email; //如果是外域账号，是否发送激活邮件，1为发送激活邮件，否则不发送激活邮件
    @Ru
    @URL
    private String ru; //注册成功后需回调到应用的url
    private boolean rtp = true;  // redirect to passport 是否跳转到 passport，若否则直接跳回 ru
    /** 语言 英文为 en */
    private String lang;

    public RegEmailApiParams(){}

    public RegEmailApiParams(String userid, String password, String createip, int client_id,String ru) {
        this.userid = userid;
        this.password = password;
        this.createip = createip;
        setClient_id(client_id);
        this.ru=ru;
    }

    public RegEmailApiParams(String userid, String password, String createip, int client_id,String ru, String lang) {
        this.userid = userid;
        this.password = password;
        this.createip = createip;
        setClient_id(client_id);
        this.ru=ru;
        this.lang=lang;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }

    public String getSend_email() {
        return send_email;
    }

    public void setSend_email(String send_email) {
        this.send_email = send_email;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
  
    public boolean isRtp() {
        return rtp;
    }
    
    public void setRtp(boolean rtp) {
        this.rtp = rtp;
    }
  
  public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }
}
