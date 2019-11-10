package com.diluv.api.utils;

import java.util.Deque;
import java.util.logging.Logger;

import org.pac4j.undertow.account.Pac4jAccount;

import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

// TODO Temp class name
public class RequestUtil {

    private static final Logger LOGGER = Logger.getLogger(RequestUtil.class.getName());

    private RequestUtil () {

    }

    public static String getParam (final HttpServerExchange exchange, String paramName) {

        Deque<String> param = exchange.getQueryParameters().get(paramName);

        if (param == null) {
            return null;
        }
        return param.peek();
    }

    public static Pac4jAccount getAccount (final HttpServerExchange exchange) {

        final SecurityContext securityContext = exchange.getSecurityContext();
        if (securityContext != null) {
            final Account account = securityContext.getAuthenticatedAccount();
            if (account instanceof Pac4jAccount) {
                return (Pac4jAccount) account;
            }
        }
        return null;
    }


}
