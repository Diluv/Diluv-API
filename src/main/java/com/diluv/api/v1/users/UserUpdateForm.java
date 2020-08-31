package com.diluv.api.v1.users;

import com.google.gson.annotations.Expose;

public class UserUpdateForm {

    @Expose
    public String password;

    @Expose
    public String displayName;

    @Expose
    public String newPassword;
}