package com.sogou.upd.passport.manager.form;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ActiveEmailParameters {

  private int client_id;
  private String passport_id;
  private String token;

  public int getClient_id() {
    return client_id;
  }

  public void setClient_id(int client_id) {
    this.client_id = client_id;
  }

  public String getPassport_id() {
    return passport_id;
  }

  public void setPassport_id(String passport_id) {
    this.passport_id = passport_id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}