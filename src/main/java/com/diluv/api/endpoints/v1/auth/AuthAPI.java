package com.diluv.api.endpoints.v1.auth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.endpoints.v1.auth.domain.LoginDomain;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.utils.ErrorType;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class AuthAPI extends RoutingHandler {

    private final UserDAO userDAO;

    public AuthAPI (UserDAO userDAO) {

        this.userDAO = userDAO;
        this.post("/v1/auth/register", this::register);
        this.post("/v1/auth/login", this::login);
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

    private Domain login (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String username = RequestUtil.getFormParam(data, "username");
            String password = RequestUtil.getFormParam(data, "password");
            String mfa = RequestUtil.getFormParam(data, "mfa");

            if (GenericValidator.isBlankOrNull(username)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Invalid username.");
            }

            if (GenericValidator.isBlankOrNull(password)) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Invalid password");
            }

            String lowerUsername = username.toLowerCase();

            UserRecord userRecord = this.userDAO.findOneByUsername(lowerUsername);
            if (userRecord == null) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Username not found");
            }

            if (!userRecord.getPasswordType().equalsIgnoreCase("bcrypt")) {
                return ResponseUtil.errorResponse(exchange, ErrorType.INTERNAL_SERVER_ERROR, "Invalid password type");
            }

            if (!OpenBSDBCrypt.checkPassword(userRecord.getPassword(), password.toCharArray())) {
                return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "Password is wrong.");
            }
            if (userRecord.isMfa()) {
                //TODO Handle
                if (mfa == null) {
                    //TODO New mfa
                    return null;
                }

                //TODO Verify MFA
                return null;
            }

            String randomKey = UUID.randomUUID().toString();

            Calendar refreshTokenExpire = Calendar.getInstance();
            refreshTokenExpire.add(Calendar.MONTH, 1);

            if (!this.userDAO.insertUserRefresh(userRecord.getId(), randomKey, new Timestamp(refreshTokenExpire.getTimeInMillis()))) {
                return ResponseUtil.errorResponse(exchange, ErrorType.INTERNAL_SERVER_ERROR, "Could not insert into DB.");
            }

            Calendar accessTokenExpire = Calendar.getInstance();
            accessTokenExpire.add(Calendar.MINUTE, 30);

            String accessToken = JWTUtil.generateAccessToken(userRecord.getId(), userRecord.getUsername(), accessTokenExpire.getTime());
            String refreshToken = JWTUtil.generateRefreshToken(userRecord.getId(), refreshTokenExpire.getTime(), randomKey);

            return ResponseUtil.successResponse(exchange, new LoginDomain(accessToken, accessTokenExpire.getTimeInMillis(), refreshToken, refreshTokenExpire.getTimeInMillis()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }
        // TODO Error
        return ResponseUtil.errorResponse(exchange, ErrorType.INTERNAL_SERVER_ERROR, "Error");
    }
}
