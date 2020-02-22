package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.DataDomain;
import com.diluv.api.endpoints.v1.Domain;
import com.diluv.api.endpoints.v1.ErrorDomain;
import com.diluv.api.utils.error.ErrorResponse;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ResponseUtil {
    
    private static Domain response (HttpServerExchange exchange, int status, Domain domain) {
        
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        if (domain != null) {
            exchange.getResponseSender().send(DiluvAPI.GSON.toJson(domain));
            exchange.endExchange();
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
