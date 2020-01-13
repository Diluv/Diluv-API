package com.diluv.api.utils.auth;

import java.text.ParseException;

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
        catch (ParseException e) {
        }
        return null;
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }

    public static String getToken (HttpServerExchange exchange) {

        return exchange.getRequestHeaders().getFirst(AUTHORIZATION);
    }
}
