package com.diluv.api.endpoints.v1.auth;

import java.util.Calendar;

import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.undertow.account.Pac4jAccount;
import org.pac4j.undertow.handler.SecurityHandler;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.auth.UserAuthenticator;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class AuthAPI extends RoutingHandler {

    public AuthAPI (UserDAO userDAO) {

        final DirectFormClient directFormClient = new DirectFormClient(new UserAuthenticator(userDAO));
        this.post("/v1/auth/register", this::register);
        this.post("/v1/auth/login", SecurityHandler.build(this::login, new Config(directFormClient), "DirectFormClient"));
    }

    private Domain register (HttpServerExchange exchange) {

        // TODO Write the register handler
        return null;
    }

    /**
     * Handles the logic request using Pac4J
     *
     * @param exchange The http request being made
     */
    private Domain login (HttpServerExchange exchange) {

        String token;
        final Pac4jAccount account = RequestUtil.getAccount(exchange);
        if (account != null) {
            final JwtGenerator<CommonProfile> jwtGenerator = new JwtGenerator<>(Constants.RSA_SIGNATURE_CONFIGURATION);

            // Makes the access expire in 30 minutes
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            jwtGenerator.setExpirationTime(calendar.getTime());
            token = jwtGenerator.generate(account.getProfile());
            exchange.getResponseSender().send(token);
            exchange.endExchange();
        }

        //TODO Error out properly, and respond.
        return null;
    }
}
