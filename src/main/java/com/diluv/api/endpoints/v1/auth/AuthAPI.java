package com.diluv.api.endpoints.v1.auth;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.RequestUtil;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.undertow.account.Pac4jAccount;
import org.pac4j.undertow.handler.SecurityHandler;

import java.util.Calendar;

public class AuthAPI extends RoutingHandler {

    public AuthAPI() {
        this.post("/v1/auth/login", SecurityHandler.build(this::login, Constants.CONFIG, Constants.REQUEST_LOGIN));
    }

    /**
     * Handles the logic request using Pac4J
     *
     * @param exchange The http request being made
     */
    private void login(HttpServerExchange exchange) {
        String token;
        final Pac4jAccount account = RequestUtil.getAccount(exchange);
        if (account != null) {
            final JwtGenerator jwtGenerator = new JwtGenerator(Constants.RSA_SIGNATURE_CONFIGURATION);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            jwtGenerator.setExpirationTime(calendar.getTime());
            token = jwtGenerator.generate(account.getProfile());
            exchange.getResponseSender().send(token);
            exchange.endExchange();
        }
    }
}
