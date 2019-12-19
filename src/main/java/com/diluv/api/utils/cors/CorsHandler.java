/*
 * Copyright (C) 2015 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.diluv.api.utils.cors;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.diluv.api.utils.Constants;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import static io.undertow.server.handlers.ResponseCodeHandler.HANDLE_200;

/**
 * Undertow handler for CORS headers.
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2014 Red Hat, inc.
 * @see "http://www.w3.org/TR/cors"
 */
public class CorsHandler implements HttpHandler {
    public static final HttpString ACCESS_CONTROL_REQUEST_METHOD = new HttpString("Access-Control-Request-Method");
    public static final HttpString ACCESS_CONTROL_REQUEST_HEADERS = new HttpString("Access-Control-Request-Headers");

    public static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN = new HttpString("Access-Control-Allow-Origin");
    public static final HttpString ACCESS_CONTROL_ALLOW_CREDENTIALS = new HttpString("Access-Control-Allow-Credentials");
    public static final HttpString ACCESS_CONTROL_MAX_AGE = new HttpString("Access-Control-Max-Age");
    public static final HttpString ACCESS_CONTROL_ALLOW_METHODS = new HttpString("Access-Control-Allow-Methods");
    public static final HttpString ACCESS_CONTROL_ALLOW_HEADERS = new HttpString("Access-Control-Allow-Headers");
    private static final long ONE_HOUR_IN_SECONDS = 60L * 60L;

    private static final List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT");
    private static final Set<String> allowedOrigins = Constants.ALLOWED_ORIGINS;

    private HttpHandler next;

    public CorsHandler (HttpHandler next) {

        this.next = next;
    }

    @Override
    public void handleRequest (HttpServerExchange exchange) throws Exception {

        if (CorsUtil.isPreflightedRequest(exchange)) {
            handlePreflightRequest(exchange);
            return;
        }
        setCorsResponseHeaders(exchange);
        this.next.handleRequest(exchange);

    }

    private void handlePreflightRequest (HttpServerExchange exchange) throws Exception {

        setCorsResponseHeaders(exchange);
        HANDLE_200.handleRequest(exchange);
    }

    private void setCorsResponseHeaders (HttpServerExchange exchange) throws Exception {

        HeaderMap headers = exchange.getRequestHeaders();
        if (headers.contains(Headers.ORIGIN) && CorsUtil.matchOrigin(exchange, allowedOrigins) != null) {
            exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_ORIGIN, headers.get(Headers.ORIGIN));
            exchange.getResponseHeaders().add(Headers.VARY, Headers.ORIGIN_STRING);
        }
        exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_METHODS, allowedMethods);
        HeaderValues requestedHeaders = headers.get(ACCESS_CONTROL_REQUEST_HEADERS);
        if (requestedHeaders != null && !requestedHeaders.isEmpty()) {
            exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_HEADERS, requestedHeaders);
        }
        else {
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.CONTENT_TYPE_STRING);
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.WWW_AUTHENTICATE_STRING);
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.AUTHORIZATION_STRING);
        }
        exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        exchange.getResponseHeaders().add(ACCESS_CONTROL_MAX_AGE, ONE_HOUR_IN_SECONDS);
    }
}