package com.diluv.api.utils.auth.tokens;

import javax.annotation.Nullable;

import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jwt.JWTClaimsSet;

public class Token {
    private final long userId;
    private final String username;

    protected Token (long userId, String username) {

        this.userId = userId;
        this.username = username;
    }

    protected static JWTClaimsSet getToken (@Nullable String token, String subject) {

        if (token != null) {

            final JWTClaimsSet claims = JWTUtil.getJWT(token);
            if (claims == null) {
                return null;
            }
            if (!claims.getSubject().equalsIgnoreCase(subject)) {
                return null;
            }
            return claims;
        }

        return null;
    }

    public long getUserId () {

        return this.userId;
    }

    public String getUsername () {

        return this.username;
    }
}
