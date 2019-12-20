package com.diluv.api.endpoints.v1.auth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.BaseUserRecord;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.endpoints.v1.auth.domain.LoginDomain;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.api.utils.error.ErrorType;
import com.nimbusds.jose.JOSEException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class AuthAPI extends RoutingHandler {

    private final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
            .setWindowSize(5);

    private final GoogleAuthenticator ga = new GoogleAuthenticator(gacb.build());
    private final UserDAO userDAO;

    public AuthAPI (UserDAO userDAO) {

        this.userDAO = userDAO;
        this.post("/v1/auth/register", this::register);
        this.post("/v1/auth/login", this::login);
        this.post("/v1/auth/verify", this::verify);
    }

    private Domain register (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formUsername = RequestUtil.getFormParam(data, "username");
            String formEmail = RequestUtil.getFormParam(data, "email");
            String formPassword = RequestUtil.getFormParam(data, "password");
            String formTerms = RequestUtil.getFormParam(data, "terms");

            if (!"true".equals(formTerms)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TERMS);
            }

            if (!Validator.validateUsername(formUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_USERNAME);
            }

            if (!EmailValidator.getInstance().isValid(formEmail)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_EMAIL);
            }

            if (GenericValidator.isBlankOrNull(formPassword) || formPassword.length() > 70 || formPassword.length() < 8) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PASSWORD);
            }

            String email = formEmail.toLowerCase();
            String username = formUsername.toLowerCase();

            if (this.userDAO.findUserIdByUsername(username) != null || this.userDAO.existTempUserByUsername(username)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.TAKEN_USERNAME);
            }
            if (this.userDAO.findUserIdByEmail(email) != null || this.userDAO.existTempUserByEmail(email)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.TAKEN_EMAIL);
            }

            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            String bcryptPassword = OpenBSDBCrypt.generate(formPassword.toCharArray(), salt, 14);

            String verificationCode = UUID.randomUUID().toString();
            if (!this.userDAO.insertTempUser(email, username, bcryptPassword, "bcrypt", verificationCode)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_TEMP_USER);
            }

            //TODO Send an email to verify
            return ResponseUtil.successResponse(exchange, null);
        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // TODO Error
        return ResponseUtil.errorResponse(exchange,ErrorResponse.INTERNAL_SERVER_ERROR);
    }

    private Domain login (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formUsername = RequestUtil.getFormParam(data, "username");
            String formPassword = RequestUtil.getFormParam(data, "password");
            String mfa = RequestUtil.getFormParam(data, "mfa");

            if (!Validator.validateUsername(formUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_USERNAME);
            }

            //TODO validate password
            if (GenericValidator.isBlankOrNull(formPassword)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PASSWORD);
            }

            String username = formUsername.toLowerCase();

            UserRecord userRecord = this.userDAO.findOneByUsername(username);
            if (userRecord == null) {
                if (this.userDAO.existTempUserByUsername(username)) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.UNVERIFIED_USER);
                }
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
            }

            if (!userRecord.getPasswordType().equalsIgnoreCase("bcrypt")) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PASSWORD_TYPE);
            }

            if (!OpenBSDBCrypt.checkPassword(userRecord.getPassword(), formPassword.toCharArray())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PASSWORD_MISMATCH);
            }

            if (userRecord.isMfa()) {
                if (mfa == null) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.REQUIRED_MFA);
                }

                if (!GenericValidator.isInt(mfa)) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_MFA);
                }

                if (!ga.authorize(userRecord.getMfaSecret(), Integer.parseInt(mfa))) {
                    //TODO Code not valid and check scratch codes
                    return null;
                }
            }

            String randomKey = UUID.randomUUID().toString();

            Calendar accessTokenExpire = Calendar.getInstance();
            accessTokenExpire.add(Calendar.MINUTE, 30);

            Calendar refreshTokenExpire = Calendar.getInstance();
            refreshTokenExpire.add(Calendar.MONTH, 1);

            if (!this.userDAO.insertUserRefresh(userRecord.getId(), randomKey, new Timestamp(refreshTokenExpire.getTimeInMillis()))) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_USER_REFRESH);
            }

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
        return ResponseUtil.errorResponse(exchange, ErrorResponse.INTERNAL_SERVER_ERROR);
    }

    private Domain verify (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formEmail = RequestUtil.getFormParam(data, "email");
            String formUsername = RequestUtil.getFormParam(data, "username");
            String formCode = RequestUtil.getFormParam(data, "code");

            //TODO Validate
            if (GenericValidator.isBlankOrNull(formEmail)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_EMAIL);
            }

            if (!Validator.validateUsername(formUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_USERNAME);
            }

            if (GenericValidator.isBlankOrNull(formCode)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_VERIFICATION_CODE);
            }

            String email = formEmail.toLowerCase();
            String username = formUsername.toLowerCase();

            BaseUserRecord tUserRecord = this.userDAO.findTempUserByEmailAndUsernameAndCode(email, username, formCode);
            if (tUserRecord == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
            }

            // Is the temp user older then 24 hours.
            if (System.currentTimeMillis() - tUserRecord.getCreatedAt().getTime() > (1000 * 60 * 60 * 24)) {
                if (!this.userDAO.deleteTempUser(tUserRecord.getEmail(), tUserRecord.getUsername())) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_TEMP_USER);
                }
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
            }

            // TODO Save avatar image from gravatar locally cache then set the logo to a sha of the image
            if (!this.userDAO.insertUser(tUserRecord.getEmail(), tUserRecord.getUsername(), tUserRecord.getPassword(), tUserRecord.getPasswordType(), "avatar.png", tUserRecord.getCreatedAt())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_USER);
            }

            if (!this.userDAO.deleteTempUser(tUserRecord.getEmail(), tUserRecord.getUsername())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_TEMP_USER);
            }

            return ResponseUtil.successResponse(exchange, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // TODO Error
        return ResponseUtil.errorResponse(exchange, ErrorResponse.INTERNAL_SERVER_ERROR);
    }
}
