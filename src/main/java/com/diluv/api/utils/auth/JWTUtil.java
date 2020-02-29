package com.diluv.api.utils.auth;

import java.text.ParseException;

import com.nimbusds.jwt.SignedJWT;

public class JWTUtil {

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
}
