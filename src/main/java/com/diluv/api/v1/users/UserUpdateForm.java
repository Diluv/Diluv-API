package com.diluv.api.v1.users;

import javax.ws.rs.FormParam;

public class UserUpdateForm {

    @FormParam("password")
    public String password;

    @FormParam("displayName")
    public String displayName;

    @FormParam("newPassword")
    public String newPassword;
}