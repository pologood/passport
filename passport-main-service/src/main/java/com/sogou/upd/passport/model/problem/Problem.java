package com.sogou.upd.passport.model.problem;

import com.sogou.upd.passport.common.CommonConstant;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-3 Time: 下午2:50 To change this template
 * use File | Settings | File Templates.
 */
public class Problem {
    private long id;
    private String passportId;
    private int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID; //问题所在产品代码,默认为1120
    private Date subTime;
    private int status = 0;   // 0-未回复, 1-已回复，2-已关闭
    private int typeId; // 问题类型
    private String title;
    private String content;
    private String email;

    public Problem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSubTime() {
        return subTime;
    }

    public void setSubTime(Date subTime) {
        this.subTime = subTime;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
