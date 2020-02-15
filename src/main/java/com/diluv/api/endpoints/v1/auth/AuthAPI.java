package com.diluv.api.endpoints.v1.auth;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.validator.GenericValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.auth.domain.LoginDomain;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.FormUtil;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.MD5Util;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.RefreshToken;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.email.EmailUtil;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.EmailSendRecord;
import com.diluv.confluencia.database.record.RefreshTokenRecord;
import com.diluv.confluencia.database.record.TempUserRecord;
import com.diluv.confluencia.database.record.UserRecord;
import com.nimbusds.jose.JOSEException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.wildbit.java.postmark.client.data.model.message.MessageResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;

public class AuthAPI extends RoutingHandler {

    private final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
            .setWindowSize(5);

    private final GoogleAuthenticator ga = new GoogleAuthenticator(gacb.build());
    private final UserDAO userDAO;
    private final EmailDAO emailDAO;

    public AuthAPI (UserDAO userDAO, EmailDAO emailDAO) {

        this.userDAO = userDAO;
        this.emailDAO = emailDAO;
        this.post("/register", this::register);
        this.post("/login", this::login);
        this.post("/verify", this::verify);
        this.post("/resend", this::resend);
        this.post("/refresh", this::refresh);
        this.get("/checkusername/{username}", this::checkUsername);
    }

    private Domain register (HttpServerExchange exchange) {

        try {
            FormData data = FormUtil.getForm(exchange);
            if (data == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
            }
            String formUsername = RequestUtil.getFormParam(data, "username");
            String formEmail = RequestUtil.getFormParam(data, "email");
            String formPassword = RequestUtil.getFormParam(data, "password");
            String formTerms = RequestUtil.getFormParam(data, "terms");

            if (!"true".equals(formTerms)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_TERMS);
            }

            if (!Validator.validateUsername(formUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
            }

            if (!Validator.validateEmail(formEmail)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_EMAIL);
            }

            if (!Validator.validatePassword(formPassword)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_PASSWORD);
            }

            String email = formEmail.toLowerCase();
            String username = formUsername.toLowerCase();

            String domain = email.split("@")[1];
            if (this.emailDAO.existsBlacklist(email, domain)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_BLACKLISTED_EMAIL);
            }

            if (this.userDAO.existsUserByUsername(username) || this.userDAO.existsTempUserByUsername(username)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_TAKEN_USERNAME);
            }
            if (this.userDAO.existsUserByEmail(email) || this.userDAO.existsTempUserByEmail(email)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_TAKEN_EMAIL);
            }

            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            String bcryptPassword = OpenBSDBCrypt.generate(formPassword.toCharArray(), salt, Constants.BCRYPT_COST);

            String verificationCode = UUID.randomUUID().toString();
            if (!this.userDAO.insertTempUser(email, username, bcryptPassword, "bcrypt", verificationCode)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_TEMP_USER);
            }

            if (Constants.POSTMARK_API_TOKEN != null) {
                MessageResponse emailResponse = EmailUtil.sendVerificationEmail(email, verificationCode);
                if (emailResponse == null) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_SEND_EMAIL);
                }

                if (!this.emailDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_EMAIL_SEND);
                }
            }
            return ResponseUtil.successResponse(exchange, null);
        }
        catch (NoSuchAlgorithmException e) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_ALGORITHM);
        }
    }

    private Domain login (HttpServerExchange exchange) {

        try {
            FormData data = FormUtil.getForm(exchange);
            if (data == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
            }
            String formUsername = RequestUtil.getFormParam(data, "username");
            String formPassword = RequestUtil.getFormParam(data, "password");
            String mfa = RequestUtil.getFormParam(data, "mfa");

            if (!Validator.validateUsername(formUsername)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
            }

            if (!Validator.validatePassword(formPassword)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_PASSWORD);
            }

            String username = formUsername.toLowerCase();

            UserRecord userRecord = this.userDAO.findOneByUsername(username);
            if (userRecord == null) {
                if (this.userDAO.existsTempUserByUsername(username)) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_NOT_VERIFIED);
                }
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
            }

            if (!userRecord.getPasswordType().equalsIgnoreCase("bcrypt")) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_PASSWORD_TYPE);
            }

            if (!OpenBSDBCrypt.checkPassword(userRecord.getPassword(), formPassword.toCharArray())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_WRONG_PASSWORD);
            }

            if (userRecord.isMfa()) {
                if (mfa == null) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_MFA);
                }

                if (!GenericValidator.isInt(mfa)) {
                    return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_MFA);
                }

                if (!ga.authorize(userRecord.getMfaSecret(), Integer.parseInt(mfa))) {
                    //TODO Code not valid and check scratch codes
                    return null;
                }
            }

            return generateToken(exchange, userRecord.getId(), userRecord.getUsername());
        }
        catch (JOSEException e) {

            DiluvAPI.LOGGER.error("Failed to login.");
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_TOKEN);
        }
    }

    private Domain verify (HttpServerExchange exchange) {

        FormData data = FormUtil.getForm(exchange);
        if (data == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
        String formEmail = RequestUtil.getFormParam(data, "email");
        String formCode = RequestUtil.getFormParam(data, "code");

        if (!Validator.validateEmail(formEmail)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_EMAIL);
        }

        if (GenericValidator.isBlankOrNull(formCode)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_VERIFICATION_CODE);
        }

        String email = formEmail.toLowerCase();

        if (this.userDAO.existsUserByEmail(email)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_VERIFIED);
        }

        TempUserRecord record = this.userDAO.findTempUserByEmailAndCode(email, formCode);
        if (record == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }

        // Is the temp user older then 24 hours.
        if (System.currentTimeMillis() - record.getCreatedAt() >= TimeUnit.DAYS.toMillis(1)) {
            if (!this.userDAO.deleteTempUser(record.getEmail(), record.getUsername())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_TEMP_USER);
            }
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }
        String emailHash = MD5Util.md5Hex(record.getEmail());

        if (emailHash == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_ALGORITHM);
        }
        BufferedImage image = ImageUtil.isValidImage("https://www.gravatar.com/avatar/" + emailHash + "?d=identicon");
        if (image == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_SAVING_IMAGE);
        }
        File file = new File(Constants.CDN_FOLDER, String.format("users/%s/avatar.png", record.getUsername()));
        if (!ImageUtil.saveImage(image, file)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_SAVING_IMAGE);
        }

        if (!this.userDAO.insertUser(record.getEmail(), record.getUsername(), record.getPassword(), record.getPasswordType(), new Timestamp(record.getCreatedAt()))) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_USER);
        }

        if (!this.userDAO.deleteTempUser(record.getEmail(), record.getUsername())) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_TEMP_USER);
        }

        return ResponseUtil.successResponse(exchange, null);
    }

    private Domain resend (HttpServerExchange exchange) {

        FormData data = FormUtil.getForm(exchange);
        if (data == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
        String formEmail = RequestUtil.getFormParam(data, "email");
        String formUsername = RequestUtil.getFormParam(data, "username");

        if (!Validator.validateEmail(formEmail)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_EMAIL);
        }

        if (!Validator.validateUsername(formUsername)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
        }

        String tempEmail = formEmail.toLowerCase();
        String username = formUsername.toLowerCase();

        TempUserRecord tUserRecord = this.userDAO.findTempUserByEmailAndUsername(tempEmail, username);
        if (tUserRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }

        // Is the temp user older then 24 hours.
        if (System.currentTimeMillis() - tUserRecord.getCreatedAt() >= TimeUnit.DAYS.toMillis(1)) {
            if (!this.userDAO.deleteTempUser(tUserRecord.getEmail(), tUserRecord.getUsername())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_TEMP_USER);
            }
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }

        String verificationCode = UUID.randomUUID().toString();

        // User record is fetched from the database to prevent ways to spoof emails.
        final String email = tUserRecord.getEmail();

        if (!this.userDAO.updateTempUser(email, username, verificationCode)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_TEMP_USER);
        }

        EmailSendRecord emailSendRecord = this.emailDAO.findEmailSentByEmailAndType(email, "verficiation");
        if (emailSendRecord != null) {
            if (System.currentTimeMillis() - tUserRecord.getCreatedAt() <= TimeUnit.HOURS.toMillis(1)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.COOLDOWN_EMAIL);
            }
        }
        if (Constants.POSTMARK_API_TOKEN != null) {
            MessageResponse emailResponse = EmailUtil.sendVerificationEmail(email, verificationCode);
            if (emailResponse == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_SEND_EMAIL);
            }

            if (!this.emailDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_EMAIL_SEND);
            }
        }
        return ResponseUtil.successResponse(exchange, null);
    }

    private Domain refresh (HttpServerExchange exchange) {

        try {
            String token = JWTUtil.getToken(exchange);

            if (token == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
            }

            RefreshToken refreshToken = RefreshToken.getToken(token);

            if (refreshToken == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_REFRESH_TOKEN);
            }

            RefreshTokenRecord record = this.userDAO.findRefreshTokenByUserIdAndCode(refreshToken.getUserId(), refreshToken.getCode());
            if (record == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER_REFRESH_TOKEN);
            }

            if (!this.userDAO.deleteRefreshTokenByUserIdAndCode(refreshToken.getUserId(), refreshToken.getCode())) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_DELETE_REFRESH_TOKEN);
            }

            return generateToken(exchange, refreshToken.getUserId(), refreshToken.getUsername());
        }
        catch (JOSEException e) {

            DiluvAPI.LOGGER.error("Failed to refresh", e);
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_TOKEN);
        }
    }

    private Domain checkUsername (HttpServerExchange exchange) {

        String username = RequestUtil.getParam(exchange, "username");
        if (username == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
        }

        if (this.userDAO.existsUserByUsername(username) || this.userDAO.existsTempUserByUsername(username)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_TAKEN_USERNAME);
        }

        return ResponseUtil.successResponse(exchange, null);
    }

    private Domain generateToken (HttpServerExchange exchange, long userId, String username) throws JOSEException {

        String code = UUID.randomUUID().toString();

        Calendar accessTokenExpire = Calendar.getInstance();
        accessTokenExpire.add(Calendar.MINUTE, 30);

        Calendar refreshTokenExpire = Calendar.getInstance();
        refreshTokenExpire.add(Calendar.MONTH, 1);

        if (!this.userDAO.insertRefreshToken(userId, code, new Timestamp(refreshTokenExpire.getTimeInMillis()))) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_USER_REFRESH);
        }
        String accessToken = new AccessToken(userId, username).generate(accessTokenExpire.getTime());
        String refreshToken = new RefreshToken(userId, username, code).generate(refreshTokenExpire.getTime());

        return ResponseUtil.successResponse(exchange, new LoginDomain(accessToken, accessTokenExpire.getTimeInMillis(), refreshToken, refreshTokenExpire.getTimeInMillis()));
    }
}
