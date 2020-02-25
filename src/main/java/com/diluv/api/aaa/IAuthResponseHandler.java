package com.diluv.api.aaa;

import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.utils.auth.AccessToken;

import io.undertow.server.HttpServerExchange;

public interface IAuthResponseHandler {
    
    IResponse handleRequest(HttpServerExchange exchange, AccessToken token) throws Exception;
}
