package com.diluv.api.endpoints.v1.auth;

import com.google.gson.annotations.Expose;

/**
 * Represents the data sent to the client when they log in to the site.
 */
public class DataLogin {
    
    /**
     * A token the client can use to authorize themselves for future requests.
     */
    @Expose
    private final String accessToken;
    
    /**
     * The point in time when the access token will be expired.
     */
    @Expose
    private final long expiredAt;
    
    /**
     * A token the client can use to refresh their access token.
     */
    @Expose
    private final String refreshToken;
    
    /**
     * The point in time when the refresh token will be expired.
     */
    @Expose
    private final long refreshExpiredAt;
    
    public DataLogin(String accessToken, long expiredAt, String refreshToken, long refreshExpiredAt) {
        
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
        this.refreshToken = refreshToken;
        this.refreshExpiredAt = refreshExpiredAt;
    }
}