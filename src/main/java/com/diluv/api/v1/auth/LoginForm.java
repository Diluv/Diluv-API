package com.diluv.api.v1.auth;

import javax.ws.rs.FormParam;

public class LoginForm {

    @FormParam("username")
    public String username;

    @FormParam("password")
    public String password;

    @FormParam("mfa")
    public String mfa;
}
