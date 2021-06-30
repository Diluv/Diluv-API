package com.diluv.api.v1.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.diluv.api.utils.validator.CustomEmail;
import com.google.gson.annotations.Expose;

public class UserUpdate {

    @NotBlank(message = "USER_INVALID_PASSWORD")
    @Size(min = 8, max = 70, message = "USER_INVALID_PASSWORD")
    @Expose
    public String currentPassword;

    @Expose
    public String displayName;

    @Size(min = 8, max = 70, message = "USER_INVALID_NEW_PASSWORD")
    @Expose
    public String newPassword;

    @CustomEmail
    @Expose
    public String email;
}