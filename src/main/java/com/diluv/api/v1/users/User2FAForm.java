package com.diluv.api.v1.users;

import com.google.gson.annotations.Expose;

public class User2FAForm {

    @Expose
    public String password;

    @Expose
    public String mfaStatus;

    @Expose
    public String mfaSecret;

    @Expose
    public Integer mfa;
}
