package com.diluv.api.utils.error;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


// TODO Temp actually handle properly
public class ErrorHandler implements HttpHandler {

    private final HttpHandler next;

    public ErrorHandler (final HttpHandler next) {

        this.next = next;
    }

    @Override
    public void handleRequest (final HttpServerExchange ex) throws Exception {

        ex.addDefaultResponseListener(exchange -> {
            if (!exchange.isResponseChannelAvailable()) {
                return false;
            }
            final int code = exchange.getStatusCode();
            if (code == 401) {
                exchange.getResponseSender().send("ERROR_401");
                return true;
            }
            else if (code == 403) {
                exchange.getResponseSender().send("ERROR_403");
                return true;
            }
            else if (code == 500) {
                exchange.getResponseSender().send("ERROR_500");
                return true;
            }
            return false;
        });
        next.handleRequest(ex);
    }
}