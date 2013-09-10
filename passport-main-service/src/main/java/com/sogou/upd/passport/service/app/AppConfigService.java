package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.AppConfig;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-25 Time: 下午11:41 To change this template
 * use File | Settings | File Templates.
 */
public interface AppConfigService {

    /*
      * 获取sms信息
      */
    public String querySmsText(int clientId, String smsCode) throws ServiceException;

    /**
     * 根据ClientId 获取AppConfig （缓存读取）
     */
    public AppConfig queryAppConfigByClientId(int clientId) throws ServiceException;

    /**
     * 根据clientId获取clientName
     */
    public String queryClientName(int clientId) throws ServiceException;
}
