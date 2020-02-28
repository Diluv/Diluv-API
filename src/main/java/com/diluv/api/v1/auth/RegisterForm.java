package com.diluv.api.v1.auth;

import javax.ws.rs.FormParam;

public class RegisterForm {

    @FormParam("email")
    public String email;

    @FormParam("username")
    public String username;

    @FormParam("password")
    public String password;

    @FormParam("terms")
    public boolean terms;
}
