package com.diluv.api.endpoints.v1.auth.domain;

public class LoginDomain {

    /**
     * Sent to the client, used to make future requests.
     */
    private String accessToken;

    /**
     * Used to tell the client when the token expires, so it can request a new one.
     */
    private long expiredAt;

    private String refreshToken;

    private long refreshExpiredAt;

    public LoginDomain (String accessToken, long expiredAt, String refreshToken, long refreshExpiredAt) {

        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
        this.refreshToken = refreshToken;
        this.refreshExpiredAt = refreshExpiredAt;
    }

    public String getAccessToken () {

        return this.accessToken;
    }

    public long getExpiredAt () {

        return this.expiredAt;
    }

    public String getRefreshToken () {

        return this.refreshToken;
    }

    public long getRefreshExpiredAt () {

        return this.refreshExpiredAt;
    }
}
