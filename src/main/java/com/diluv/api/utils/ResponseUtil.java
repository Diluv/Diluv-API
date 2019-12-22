package com.diluv.api.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.domain.DataDomain;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.utils.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ResponseUtil {
    private static final Logger LOGGER = Logger.getLogger(ResponseUtil.class.getName());

    private static Domain response (HttpServerExchange exchange, int status, Domain domain) {

        exchange.setStatusCode(status);
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        if (domain != null) {
            try {
                exchange.getResponseSender().send(DiluvAPI.MAPPER.writeValueAsString(domain));
                exchange.endExchange();
            }
            catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Error writing json", e);
                exchange.setStatusCode(500);
                exchange.endExchange();
                return null;
            }
        }
        return domain;
    }

    public static Domain successResponse (HttpServerExchange exchange, Object data) {

        return response(exchange, 200, data == null ? null : new DataDomain<>(data));
    }

    public static Domain errorResponse (HttpServerExchange exchange, ErrorResponse errorResponses) {

        return response(exchange, errorResponses.getType().getCode(), new ErrorDomain(errorResponses.getType().getError(), errorResponses.getMessage()));
    }
}
