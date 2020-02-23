package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.DataResponse;
import com.diluv.api.endpoints.v1.ErrorResponse;
import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.utils.error.ErrorMessage;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public final class ResponseUtil {
    
    private static IResponse jsonResponse (HttpServerExchange exchange, int status, IResponse response) {
        
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        
        if (response != null) {
            
            exchange.getResponseSender().send(DiluvAPI.GSON.toJson(response));
            exchange.endExchange();
        }
        
        return response;
    }
    
    public static IResponse successResponse (HttpServerExchange exchange, Object data) {
        
        return jsonResponse(exchange, 200, data == null ? null : new DataResponse<>(data));
    }
    
    public static IResponse errorResponse (HttpServerExchange exchange, ErrorMessage errorResponses) {
        
        return jsonResponse(exchange, errorResponses.getType().getCode(), new ErrorResponse(errorResponses.getType().getError(), errorResponses.getMessage()));
    }
}