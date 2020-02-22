package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.DataResponse;
import com.diluv.api.endpoints.v1.ErrorResponse;
import com.diluv.api.endpoints.v1.Response;
import com.diluv.api.utils.error.ErrorMessage;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ResponseUtil {
    
    private static Response response (HttpServerExchange exchange, int status, Response domain) {
        
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        if (domain != null) {
            exchange.getResponseSender().send(DiluvAPI.GSON.toJson(domain));
            exchange.endExchange();
        }
        return domain;
    }
    
    public static Response successResponse (HttpServerExchange exchange, Object data) {
        
        return response(exchange, 200, data == null ? null : new DataResponse<>(data));
    }
    
    public static Response errorResponse (HttpServerExchange exchange, ErrorMessage errorResponses) {
        
        return response(exchange, errorResponses.getType().getCode(), new ErrorResponse(errorResponses.getType().getError(), errorResponses.getMessage()));
    }
}