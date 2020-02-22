package com.diluv.api.endpoints.v1.auth;

/**
 * Represents the data sent to the client when they log in to the site.
 */
public class DataLogin {
    
    /**
     * A token the client can use to authorize themselves for future requests.
     */
    private final String accessToken;
    
    /**
     * The point in time when the access token will be expired.
     */
    private final long expiredAt;
    
    /**
     * A token the client can use to refresh their access token.
     */
    private final String refreshToken;
    
    /**
     * The point in time when the refresh token will be expired.
     */
    private final long refreshExpiredAt;
    
    public DataLogin(String accessToken, long expiredAt, String refreshToken, long refreshExpiredAt) {
        
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
