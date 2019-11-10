package com.diluv.api.endpoints.v1.auth.domain;

import com.diluv.api.endpoints.v1.domain.Domain;

public class LoginDomain implements Domain {

    /**
     * Sent to the client, used to make future requests.
     */
    private String accessToken;

    /**
     * Used to tell the client when the token expires, so it can request a new one.
     */
    private long expiredAt;

    public LoginDomain (String accessToken) {

        this.accessToken = accessToken;
    }

    public String getAccessToken () {

        return accessToken;
    }
}
