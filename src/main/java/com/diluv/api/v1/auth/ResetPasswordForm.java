package com.diluv.api.v1.auth;

import javax.ws.rs.FormParam;

public class ResetPasswordForm {

    @FormParam("email")
    public String email;

    @FormParam("code")
    public String code;

    @FormParam("password")
    public String password;
}
