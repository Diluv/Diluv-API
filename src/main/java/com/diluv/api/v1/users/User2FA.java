package com.diluv.api.v1.users;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotBlank;

public class User2FA {

    @NotBlank(message = "USER_INVALID_PASSWORD")
    @Expose
    public String password;

    @Expose
    public String mfaStatus;

    @Expose
    public String mfaSecret;

    @Expose
    public Integer mfa;
}
