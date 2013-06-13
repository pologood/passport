package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 下午3:08
 */
public class UpdateBindMobileApiParams extends BaseApiParams {

    @NotBlank(message = "要换绑定用户不能为空")
    private String userid;

    @NotBlank(message = "旧手机号不能为空")
    private String oldMobile;

    @NotBlank(message = "新绑定手机号不能为空")
    private String newMobile;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOldMobile() {
        return oldMobile;
    }

    public void setOldMobile(String oldMobile) {
        this.oldMobile = oldMobile;
    }

    public String getNewMobile() {
        return newMobile;
    }

    public void setNewMobile(String newMobile) {
        this.newMobile = newMobile;
    }
}
