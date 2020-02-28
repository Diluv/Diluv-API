package com.diluv.api.v1.auth;

import javax.ws.rs.FormParam;

public class VerifyForm {

    @FormParam("email")
    public String email;

    @FormParam("code")
    public String code;

}
