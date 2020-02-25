package com.diluv.api.aaa;

import com.diluv.api.endpoints.v1.IResponse;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ResponseHandler implements HttpHandler {

    final IResponseHandler handler;
    
    public ResponseHandler(IResponseHandler handler) {
        
        this.handler = handler;
    }

    @Override
    public void handleRequest (HttpServerExchange exchange) throws Exception {
                
        final IResponse response = this.handler.handleRequest(exchange);
    }
    
    public interface IResponseHandler {
        
        IResponse handleRequest(HttpServerExchange exchange) throws Exception;
    }
}