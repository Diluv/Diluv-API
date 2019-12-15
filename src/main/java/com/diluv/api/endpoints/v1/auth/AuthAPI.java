package com.diluv.api.endpoints.v1.auth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.undertow.account.Pac4jAccount;
import org.pac4j.undertow.handler.SecurityHandler;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ErrorType;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.UserAuthenticator;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class AuthAPI extends RoutingHandler {

    private final SecureRandom RANDOM = new SecureRandom();
    private final UserDAO userDAO;

    public AuthAPI (UserDAO userDAO) {

        this.userDAO = userDAO;
        final DirectFormClient directFormClient = new DirectFormClient(new UserAuthenticator(userDAO));
        this.post("/v1/auth/register", this::register);
        this.post("/v1/auth/login", SecurityHandler.build(this::login, new Config(directFormClient), "DirectFormClient"));
    }

    private Domain register (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String username = RequestUtil.getFormParam(data, "username");
            String email = RequestUtil.getFormParam(data, "email");
            String password = RequestUtil.getFormParam(data, "password");
            String terms = RequestUtil.getFormParam(data, "terms");

            if (!"true".equals(terms)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Need to agree to terms");
            }

            if (!EmailValidator.getInstance().isValid(email)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Invalid email");
            }

            if (GenericValidator.isBlankOrNull(username) || username.length() > 50 || username.length() < 3) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Username is not valid.");
            }
            if (!GenericValidator.matchRegexp(username, "([A-Za-z0-9-_]+)")) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Username must be A-Za-z0-9-_");
            }

            if (GenericValidator.isBlankOrNull(password) || password.length() > 70 || password.length() < 8) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Invalid password");
            }

            String lowerEmail = email.toLowerCase();
            String lowerUsername = username.toLowerCase();

            if (this.userDAO.findUserIdByUsername(lowerUsername) != null || this.userDAO.existTempUserByUsername(lowerUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Username already taken");
            }
            if (this.userDAO.findUserIdByEmail(lowerEmail) != null || this.userDAO.existTempUserByEmail(lowerEmail)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Email already used.");
            }

            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            String bcryptPassword = OpenBSDBCrypt.generate(password.toCharArray(), salt, 14);

            String verificationCode = UUID.randomUUID().toString();
            if (!this.userDAO.insertTempUser(lowerEmail, lowerUsername, bcryptPassword, "bcrypt", "avatar.png", verificationCode)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.INTERNAL_SERVER_ERROR, "Failed to create user.");
            }

            //TODO Send an email to verify
            return ResponseUtil.successResponse(exchange, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // TODO Error
        return ResponseUtil.errorResponse(exchange, ErrorType.INTERNAL_SERVER_ERROR, "Error");
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
