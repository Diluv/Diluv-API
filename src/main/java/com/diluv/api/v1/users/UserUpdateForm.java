package com.diluv.api.v1.users;

import com.google.gson.annotations.Expose;

public class UserUpdateForm {

    @Expose
    public String currentPassword;

    @Expose
    public String displayName;

    @Expose
    public String newPassword;

    @Expose
    public String email;
}