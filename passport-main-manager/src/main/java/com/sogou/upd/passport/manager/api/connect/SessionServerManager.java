package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;

/**
 * User: mayan
 * Date: 13-12-4
 * Time: 下午8:43
 */
public interface SessionServerManager {
     public Result createSession(String userId);
     public Result createSession(String passportId, String weixinOpenId, boolean isWap);
     public Result removeSession(String sgid);
     public Result getPassportIdBySgid(String sgid,String ip);
     public Result verifySid(String sgid,String ip);
     public Result verifySid(String sgid, int clientId, String ip);
}
