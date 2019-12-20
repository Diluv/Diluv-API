package com.diluv.api.utils;

import java.util.Deque;
import java.util.logging.Logger;

import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jwt.SignedJWT;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

// TODO Temp class name
public class RequestUtil {

    private static final Logger LOGGER = Logger.getLogger(RequestUtil.class.getName());
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private RequestUtil () {

    }

    public static String getFormParam (final FormData data, String paramName) {

        Deque<FormData.FormValue> param = data.get(paramName);

        if (param == null) {
            return null;
        }
        FormData.FormValue formValue = param.peekFirst();
        if (formValue == null) {
            return null;
        }
        return formValue.getValue();
    }

    public static String getParam (final HttpServerExchange exchange, String paramName) {

        Deque<String> param = exchange.getQueryParameters().get(paramName);

        if (param == null) {
            return null;
        }
        return param.peek();
    }

    public static Long getUserIdFromToken (String token) {

        if (RequestUtil.isBearerToken(token)) {
            SignedJWT jwt = JWTUtil.getJWT(token);
            if (jwt != null) {
                return JWTUtil.getUserId(jwt);
            }
        }
        return null;
    }

    public static String getUsernameFromToken (String token) {

        if (RequestUtil.isBearerToken(token)) {
            SignedJWT jwt = JWTUtil.getJWT(token);
            if (jwt != null) {
                return JWTUtil.getUsername(jwt);
            }
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