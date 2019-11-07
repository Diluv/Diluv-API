package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.domain.DataDomain;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.pac4j.undertow.account.Pac4jAccount;

import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Temp class name
public class RequestUtil {

    private static final Logger LOGGER = Logger.getLogger(RequestUtil.class.getName());

    private RequestUtil() {
    }

    public static Pac4jAccount getAccount(final HttpServerExchange exchange) {
        final SecurityContext securityContext = exchange.getSecurityContext();
        if (securityContext != null) {
            final Account account = securityContext.getAuthenticatedAccount();
            if (account instanceof Pac4jAccount) {
                return (Pac4jAccount) account;
            }
        }
        return null;
    }

    private static Domain response(HttpServerExchange exchange, int status, Domain domain) {
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        if (domain != null) {
            try {
                exchange.getResponseSender().send(DiluvAPI.MAPPER.writeValueAsString(domain));
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Error writing json", e);
                exchange.setStatusCode(500);
                exchange.endExchange();
                return null;
            }
        }
        return domain;
    }

    public static Domain successResponse(HttpServerExchange exchange, Object data) {
        return response(exchange, 200, new DataDomain(data));
    }

    public static Domain errorResponse(HttpServerExchange exchange, ErrorType errorType, String message) {
        return response(exchange, errorType.getCode(), new ErrorDomain(errorType, message));
    }
}
