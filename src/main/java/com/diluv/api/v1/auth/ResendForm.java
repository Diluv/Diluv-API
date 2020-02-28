package com.diluv.api.v1.auth;

import javax.ws.rs.FormParam;

public class ResendForm {

    @FormParam("email")
    public String email;

    @FormParam("username")
    public String username;
}
