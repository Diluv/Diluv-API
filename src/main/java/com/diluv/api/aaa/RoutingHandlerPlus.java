package com.diluv.api.aaa;

import java.util.Deque;

import javax.annotation.Nullable;

import com.diluv.api.aaa.ResponseHandler.IResponseHandler;
import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorMessage;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class RoutingHandlerPlus extends RoutingHandler {
    
    public void get(String path, String first, HandlerPar1<String> handler) {
        
        this.get(path, new ResponseHandler(e -> handler.accept(e, pathString(e, first))));
    }
    
    public void get(String path, String first, String second, BiHandlerPar2<String, String> handler) {
        
        this.get(path, new ResponseHandler(e -> handler.accept(e, pathString(e, first), pathString(e, second))));
    }
    
    public void get(String path, String first, String second, String third, TriHandlerPar3<String, String, String> handler) {
        
        this.get(path, new ResponseHandler(e -> handler.accept(e, pathString(e, first), pathString(e, second), pathString(e, third))));
    }
    
    public void get(String path, AuthHandler handler) {
        
        this.get(path, authenticate((e, a) -> handler.accept(e, a)));
    }
    
    public void get(String path, String first, AuthHandlerPar1<String> handler) {
        
        this.get(path, authenticate((e, a) -> handler.accept(e, a, pathString(e, first))));
    }
    
    public void get(String path, String first, String second, AuthHandlerPar2<String, String> handler) {
        
        this.get(path, authenticate((e, a)-> handler.accept(e, a, pathString(e, first), pathString(e, second))));
    }
    
    public void get(String path, String first, String second, String third, AuthHandlerPar3<String, String, String> handler) {
        
        this.get(path, authenticate((e, a) -> handler.accept(e, a, pathString(e, first), pathString(e, second), pathString(e, third))));
    }
    
    public void get(String path, IResponseHandler handler) {
        
        this.get(path, new ResponseHandler(handler));
    }
    
    @Nullable
    public static String pathString (HttpServerExchange exchange, String name) {
        
        final Deque<String> param = exchange.getPathParameters().get(name);
        return param != null ? param.peekFirst() : null;
    }
    
    @Nullable
    public static String queryString(HttpServerExchange exchange, String name) {
        
        final Deque<String> param = exchange.getQueryParameters().get(name);
        return param != null ? param.peekFirst() : null;
    }
    
    @FunctionalInterface
    public interface HandlerPar1<A> {

        IResponse accept(HttpServerExchange exchance, A par1);
    }
    
    @FunctionalInterface
    public interface BiHandlerPar2<A, B> {

        IResponse accept(HttpServerExchange exchange, A par1, B par2);
    }
    
    @FunctionalInterface
    public interface TriHandlerPar3<A, B, C> {

        IResponse accept(HttpServerExchange exchange, A par1, B par2, C par3);
    }
    
    public interface AuthHandler {

        IResponse accept(HttpServerExchange exchance, AccessToken token);
    }
    
    @FunctionalInterface
    public interface AuthHandlerPar1<A> {

        IResponse accept(HttpServerExchange exchance, AccessToken token, A par1);
    }
    
    @FunctionalInterface
    public interface AuthHandlerPar2<A, B> {

        IResponse accept(HttpServerExchange exchange, AccessToken token, A par1, B par2);
    }
    
    @FunctionalInterface
    public interface AuthHandlerPar3<A, B, C> {

        IResponse accept(HttpServerExchange exchange, AccessToken token, A par1, B par2, C par3);
    }
    
    
    private IResponseHandler authenticate (IAuthResponseHandler handler) {
        
        return exchange -> {
            
            final AccessToken token = JWTUtil.getToken(exchange);
            
            if (token == null) {
                
                return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_NOT_AUTHORIZED);
            }
            
            return handler.handleRequest(exchange, token);
        };
    }    
    
    public interface IAuthResponseHandler {
        
        IResponse handleRequest(HttpServerExchange exchange, AccessToken token) throws Exception;
    }
}