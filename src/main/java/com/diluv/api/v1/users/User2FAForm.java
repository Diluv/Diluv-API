package com.diluv.api.v1.users;

import javax.ws.rs.FormParam;

public class User2FAForm {

    @FormParam("password")
    public String password;

    @FormParam("mfaStatus")
    public String mfaStatus;

    @FormParam("mfaSecret")
    public String mfaSecret;

    @FormParam("mfa")
    public Integer mfa;
}
