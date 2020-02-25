package com.diluv.api.utils.auth;

import java.text.ParseException;

import javax.annotation.Nullable;

import com.nimbusds.jwt.SignedJWT;

import io.undertow.server.HttpServerExchange;

public class JWTUtil {
    
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    
    public static SignedJWT getJWT (String token) {
        
        try {
            if (JWTUtil.isBearerToken(token)) {
                
                return SignedJWT.parse(token.substring(JWTUtil.BEARER.length()));
            }
        }
        catch (final ParseException e) {
            
            // Skip over invalid JWT without logging an error.
        }
        return null;
    }
    
    public static boolean isBearerToken (String token) {
        
        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }
    
    public static String getTokenString (HttpServerExchange exchange) {
        
        return exchange.getRequestHeaders().getFirst(AUTHORIZATION);
    }
    
    @Nullable
    public static AccessToken getTokenSafely (HttpServerExchange exchange) {
        
        try {
            
            return getToken(exchange);
        }
        
        catch (InvalidTokenException e) {
            
            return null;
        }
    }
    
    public static AccessToken getToken (HttpServerExchange exchange) throws InvalidTokenException {
        
        final String token = getTokenString(exchange);
        if (token != null) {
            final AccessToken accessToken = AccessToken.getToken(token);
            if (accessToken == null) {
                throw new InvalidTokenException();
            }
            
            return accessToken;
        }
        
        return null;
    }
}
