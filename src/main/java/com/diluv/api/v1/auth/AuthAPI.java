package com.diluv.api.v1.auth;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.GenericValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.data.DataLogin;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.RefreshToken;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.email.EmailUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.EmailSendRecord;
import com.diluv.confluencia.database.record.RefreshTokenRecord;
import com.diluv.confluencia.database.record.TempUserRecord;
import com.diluv.confluencia.database.record.UserRecord;
import com.nimbusds.jose.JOSEException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.wildbit.java.postmark.client.data.model.message.MessageResponse;

import static com.diluv.api.Main.DATABASE;

@Path("/auth")
public class AuthAPI {

    private final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30)).setWindowSize(5);

    private final GoogleAuthenticator ga = new GoogleAuthenticator(this.gacb.build());

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf (@FormParam("email") String formEmail, @FormParam("username") String formUsername, @FormParam("password") String formPassword, @FormParam("terms") boolean formTerms) {

        try {
            if (!formTerms) {
                return ErrorMessage.USER_INVALID_TERMS.respond();
            }

            if (!Validator.validateUsername(formUsername)) {
                return ErrorMessage.USER_INVALID_USERNAME.respond();
            }

            if (!Validator.validateEmail(formEmail)) {
                return ErrorMessage.USER_INVALID_EMAIL.respond();
            }

            if (!Validator.validatePassword(formPassword)) {
                return ErrorMessage.USER_INVALID_PASSWORD.respond();
            }

            final String email = formEmail.toLowerCase();
            final String username = formUsername.toLowerCase();

            final String domain = email.split("@")[1];
            if (DATABASE.emailDAO.existsBlacklist(email, domain)) {
                return ErrorMessage.USER_BLACKLISTED_EMAIL.respond();
            }

            if (DATABASE.userDAO.existsUserByUsername(username) || DATABASE.userDAO.existsTempUserByUsername(username)) {
                return ErrorMessage.USER_TAKEN_USERNAME.respond();
            }
            if (DATABASE.userDAO.existsUserByEmail(email) || DATABASE.userDAO.existsTempUserByEmail(email)) {
                return ErrorMessage.USER_TAKEN_EMAIL.respond();
            }

            final byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            final String bcryptPassword = OpenBSDBCrypt.generate(formPassword.toCharArray(), salt, Constants.BCRYPT_COST);

            final String verificationCode = UUID.randomUUID().toString();
            if (!DATABASE.userDAO.insertTempUser(email, username, bcryptPassword, "bcrypt", verificationCode)) {
                return ErrorMessage.FAILED_CREATE_TEMP_USER.respond();
            }

            if (Constants.POSTMARK_API_TOKEN != null) {
                final MessageResponse emailResponse = EmailUtil.sendVerificationEmail(email, verificationCode);
                if (emailResponse == null) {
                    return ErrorMessage.FAILED_SEND_EMAIL.respond();
                }

                if (!DATABASE.emailDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                    return ErrorMessage.FAILED_CREATE_EMAIL_SEND.respond();
                }
            }
            return ResponseUtil.successResponse(null);
        }
        catch (final NoSuchAlgorithmException e) {
            return ErrorMessage.ERROR_ALGORITHM.respond();
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login (@FormParam("username") String formUsername, @FormParam("password") String formPassword, @FormParam("mfa") String formMFA) {

        try {

            if (!Validator.validateUsername(formUsername)) {
                return ErrorMessage.USER_INVALID_USERNAME.respond();
            }

            if (!Validator.validatePassword(formPassword)) {
                return ErrorMessage.USER_INVALID_PASSWORD.respond();
            }

            final String username = formUsername.toLowerCase();

            final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(username);
            if (userRecord == null) {
                if (DATABASE.userDAO.existsTempUserByUsername(username)) {
                    return ErrorMessage.USER_NOT_VERIFIED.respond();
                }
                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            if (!userRecord.getPasswordType().equalsIgnoreCase("bcrypt")) {
                return ErrorMessage.USER_INVALID_PASSWORD_TYPE.respond();
            }

            if (!OpenBSDBCrypt.checkPassword(userRecord.getPassword(), formPassword.toCharArray())) {
                return ErrorMessage.USER_WRONG_PASSWORD.respond();
            }

            if (userRecord.isMfa()) {
                if (formMFA == null) {
                    return ErrorMessage.USER_REQUIRED_MFA.respond();
                }

                if (!GenericValidator.isInt(formMFA)) {
                    return ErrorMessage.USER_INVALID_MFA.respond();
                }

                if (!this.ga.authorize(userRecord.getMfaSecret(), Integer.parseInt(formMFA))) {
                    // TODO Code not valid and check scratch codes
                    return null;
                }
            }

            return this.generateToken(userRecord.getId(), userRecord.getUsername());
        }
        catch (final JOSEException e) {

            DiluvAPIServer.LOGGER.error("Failed to login.");
            return ErrorMessage.ERROR_TOKEN.respond();
        }
    }

    @POST
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify (@FormParam("email") String formEmail, @FormParam("code") String formCode) {

        if (!Validator.validateEmail(formEmail)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        if (GenericValidator.isBlankOrNull(formCode)) {
            return ErrorMessage.USER_INVALID_VERIFICATION_CODE.respond();
        }

        final String email = formEmail.toLowerCase();

        if (DATABASE.userDAO.existsUserByEmail(email)) {
            return ErrorMessage.USER_VERIFIED.respond();
        }

        final TempUserRecord record = DATABASE.userDAO.findTempUserByEmailAndCode(email, formCode);
        if (record == null) {
            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        // Is the temp user older then 24 hours.
        if (System.currentTimeMillis() - record.getCreatedAt() >= TimeUnit.DAYS.toMillis(1)) {
            if (!DATABASE.userDAO.deleteTempUser(record.getEmail(), record.getUsername())) {
                return ErrorMessage.FAILED_DELETE_TEMP_USER.respond();
            }
            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        final String emailHashHex = DigestUtils.md5Hex(record.getEmail().trim().toLowerCase());

        if (emailHashHex == null) {
            return ErrorMessage.ERROR_ALGORITHM.respond();
        }

        final BufferedImage image = ImageUtil.isValidImage("https://www.gravatar.com/avatar/" + emailHashHex + "?d=identicon");
        if (image == null) {
            return ErrorMessage.ERROR_SAVING_IMAGE.respond();
        }
        final File file = new File(Constants.CDN_FOLDER, String.format("users/%s/avatar.png", record.getUsername()));
        if (!ImageUtil.saveImage(image, file)) {
            return ErrorMessage.ERROR_SAVING_IMAGE.respond();
        }

        if (!DATABASE.userDAO.insertUser(record.getEmail(), record.getUsername(), record.getPassword(), record.getPasswordType(), new Timestamp(record.getCreatedAt()))) {
            return ErrorMessage.FAILED_CREATE_USER.respond();
        }

        if (!DATABASE.userDAO.deleteTempUser(record.getEmail(), record.getUsername())) {
            return ErrorMessage.FAILED_DELETE_TEMP_USER.respond();
        }

        return ResponseUtil.successResponse(null);
    }

    @POST
    @Path("/resend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resend (@FormParam("email") String formEmail, @FormParam("username") String formUsername) {

        if (!Validator.validateEmail(formEmail)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        if (!Validator.validateUsername(formUsername)) {
            return ErrorMessage.USER_INVALID_USERNAME.respond();
        }

        final String tempEmail = formEmail.toLowerCase();
        final String username = formUsername.toLowerCase();

        final TempUserRecord tUserRecord = DATABASE.userDAO.findTempUserByEmailAndUsername(tempEmail, username);
        if (tUserRecord == null) {
            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        // Is the temp user older then 24 hours.
        if (System.currentTimeMillis() - tUserRecord.getCreatedAt() >= TimeUnit.DAYS.toMillis(1)) {
            if (!DATABASE.userDAO.deleteTempUser(tUserRecord.getEmail(), tUserRecord.getUsername())) {
                return ErrorMessage.FAILED_DELETE_TEMP_USER.respond();
            }
            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        final String verificationCode = UUID.randomUUID().toString();

        // User record is fetched from the database to prevent ways to spoof emails.
        final String email = tUserRecord.getEmail();

        if (!DATABASE.userDAO.updateTempUser(email, username, verificationCode)) {
            return ErrorMessage.FAILED_CREATE_TEMP_USER.respond();
        }

        final EmailSendRecord emailSendRecord = DATABASE.emailDAO.findEmailSentByEmailAndType(email, "verficiation");
        if (emailSendRecord != null) {
            if (System.currentTimeMillis() - tUserRecord.getCreatedAt() <= TimeUnit.HOURS.toMillis(1)) {
                return ErrorMessage.COOLDOWN_EMAIL.respond();
            }
        }
        if (Constants.POSTMARK_API_TOKEN != null) {
            final MessageResponse emailResponse = EmailUtil.sendVerificationEmail(email, verificationCode);
            if (emailResponse == null) {
                return ErrorMessage.FAILED_SEND_EMAIL.respond();
            }

            if (!DATABASE.emailDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                return ErrorMessage.FAILED_CREATE_EMAIL_SEND.respond();
            }
        }
        return ResponseUtil.successResponse(null);
    }

    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh (@HeaderParam("Authorization") String auth) {

        try {
            if (auth == null) {
                return ErrorMessage.USER_REQUIRED_TOKEN.respond();
            }

            final RefreshToken refreshToken = RefreshToken.getToken(auth);

            if (refreshToken == null) {
                return ErrorMessage.USER_INVALID_REFRESH_TOKEN.respond();
            }

            final RefreshTokenRecord record = DATABASE.userDAO.findRefreshTokenByUserIdAndCode(refreshToken.getUserId(), refreshToken.getCode());
            if (record == null) {
                return ErrorMessage.NOT_FOUND_USER_REFRESH_TOKEN.respond();
            }

            if (!DATABASE.userDAO.deleteRefreshTokenByUserIdAndCode(refreshToken.getUserId(), refreshToken.getCode())) {
                return ErrorMessage.FAILED_DELETE_REFRESH_TOKEN.respond();
            }

            return this.generateToken(refreshToken.getUserId(), refreshToken.getUsername());
        }
        catch (final JOSEException e) {

            DiluvAPIServer.LOGGER.error("Failed to refresh", e);
            return ErrorMessage.ERROR_TOKEN.respond();
        }
    }

    @GET
    @Path("/checkusername/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUsername (@PathParam("username") String username) {

        if (DATABASE.userDAO.existsUserByUsername(username) || DATABASE.userDAO.existsTempUserByUsername(username)) {
            return ErrorMessage.USER_TAKEN_USERNAME.respond();
        }

        return ResponseUtil.successResponse(null);
    }

    private Response generateToken (long userId, String username) throws JOSEException {

        final String code = UUID.randomUUID().toString();

        final Calendar accessTokenExpire = Calendar.getInstance();
        accessTokenExpire.add(Calendar.MINUTE, 30);

        final Calendar refreshTokenExpire = Calendar.getInstance();
        refreshTokenExpire.add(Calendar.MONTH, 1);

        if (!DATABASE.userDAO.insertRefreshToken(userId, code, new Timestamp(refreshTokenExpire.getTimeInMillis()))) {
            return ErrorMessage.FAILED_CREATE_USER_REFRESH.respond();
        }
        final String accessToken = new AccessToken(userId, username).generate(accessTokenExpire.getTime());
        final String refreshToken = new RefreshToken(userId, username, code).generate(refreshTokenExpire.getTime());

        return ResponseUtil.successResponse(new DataLogin(accessToken, accessTokenExpire.getTimeInMillis(), refreshToken, refreshTokenExpire.getTimeInMillis()));
    }
}