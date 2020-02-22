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

import static io.undertow.util.Headers.ORIGIN;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.HeaderMap;
import io.undertow.util.Methods;
import io.undertow.util.NetworkUtils;

/**
 * Utility class for CORS handling.
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2014 Red Hat, inc.
 */
public final class CorsUtil {
    private static final Logger logger = LoggerFactory.getLogger(CorsUtil.class);
    
    private CorsUtil() {
        
    }
    
    /**
     * Checks to see if the request is cors
     *
     * @param headers The list of headers sent in the request
     * @return True if the request headers contains origins or access control headers
     */
    public static boolean isCorsRequest (HeaderMap headers) {
        
        return headers.contains(ORIGIN) || headers.contains(CorsHandler.ACCESS_CONTROL_REQUEST_HEADERS) || headers.contains(CorsHandler.ACCESS_CONTROL_REQUEST_METHOD);
    }
    
    /**
     * Match the Origin header with the allowed origins. If it doesn't match then a 403
     * response code is set on the response and it returns null.
     *
     * @param exchange the current HttpExchange.
     * @param allowedOrigins list of sanitized allowed origins.
     * @return the first matching origin, null otherwise.
     * @throws Exception the checked exception
     */
    public static String matchOrigin (HttpServerExchange exchange, Collection<String> allowedOrigins) throws Exception {
        
        final HeaderMap headers = exchange.getRequestHeaders();
        final String[] origins = headers.get(ORIGIN).toArray();
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            for (final String allowedOrigin : allowedOrigins) {
                for (final String origin : origins) {
                    if (allowedOrigin.equalsIgnoreCase(sanitizeDefaultPort(origin))) {
                        return allowedOrigin;
                    }
                }
            }
        }
        final String allowedOrigin = defaultOrigin(exchange);
        for (final String origin : origins) {
            if (allowedOrigin.equalsIgnoreCase(sanitizeDefaultPort(origin))) {
                return allowedOrigin;
            }
        }
        logger.debug("Request rejected due to HOST/ORIGIN mis-match.");
        ResponseCodeHandler.HANDLE_403.handleRequest(exchange);
        return null;
    }
    
    /**
     * Determine the default origin, to allow for local access.
     *
     * @param exchange the current HttpExchange.
     * @return the default origin (aka current server).
     */
    public static String defaultOrigin (HttpServerExchange exchange) {
        
        final String host = NetworkUtils.formatPossibleIpv6Address(exchange.getHostName());
        final String protocol = exchange.getRequestScheme();
        final int port = exchange.getHostPort();
        // This browser set header should not need IPv6 escaping
        final StringBuilder allowedOrigin = new StringBuilder(256);
        allowedOrigin.append(protocol).append("://").append(host);
        if (!isDefaultPort(port, protocol)) {
            allowedOrigin.append(':').append(port);
        }
        return allowedOrigin.toString();
    }
    
    /**
     * Checks to see if the request is being made on the default port
     *
     * @param port The port the request is being made on
     * @param protocol The protocol the request is using
     * @return True if the port and protocol port match else returns false
     */
    private static boolean isDefaultPort (int port, String protocol) {
        
        return "http".equals(protocol) && 80 == port || "https".equals(protocol) && 443 == port;
    }
    
    /**
     * Removes the port from a URL if this port is the default one for the URL's scheme.
     *
     * @param url the url to be sanitized.
     * @return the sanitized url.
     */
    public static String sanitizeDefaultPort (String url) {
        
        final int afterSchemeIndex = url.indexOf("://");
        if (afterSchemeIndex < 0) {
            return url;
        }
        final String scheme = url.substring(0, afterSchemeIndex);
        int fromIndex = scheme.length() + 3;
        // Let's see if it is an IPv6 Address
        final int ipv6StartIndex = url.indexOf('[', fromIndex);
        if (ipv6StartIndex > 0) {
            fromIndex = url.indexOf(']', ipv6StartIndex);
        }
        final int portIndex = url.indexOf(':', fromIndex);
        if (portIndex >= 0) {
            final int port = Integer.parseInt(url.substring(portIndex + 1));
            if (isDefaultPort(port, scheme)) {
                return url.substring(0, portIndex);
            }
        }
        return url;
    }
    
    /**
     * Checks to see if a preflight request is being made
     *
     * @param exchange The request exchange
     * @return True if the request is a preflight request
     */
    public static boolean isPreflightedRequest (HttpServerExchange exchange) {
        
        return Methods.OPTIONS.equals(exchange.getRequestMethod()) && isCorsRequest(exchange.getRequestHeaders());
    }
}