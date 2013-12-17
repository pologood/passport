package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Password;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class BaseLoginParams {

    /**
     * 登陆用户名
     */
    @Length(min = 1, max = 100, message = "用户名错误，请重新输入！")
    @NotBlank(message = "请输入用户名！")
    private String username;

    /**
     * 登陆密码
     */
    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    @NotBlank(message = "请输入密码！")
    private String password;

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;//验证码
    private String token;//标识码

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            password = password.trim();
        }
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}