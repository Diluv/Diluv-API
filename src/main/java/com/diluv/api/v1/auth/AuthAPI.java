package com.diluv.api.v1.auth;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.PasswordUtility;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.email.EmailUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.EmailSendRecord;
import com.diluv.confluencia.database.record.PasswordResetRecord;
import com.diluv.confluencia.database.record.TempUserRecord;
import com.diluv.confluencia.database.record.UserRecord;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.wildbit.java.postmark.client.data.model.message.MessageResponse;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthAPI {

    private final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
            .setWindowSize(5);

    private final GoogleAuthenticator ga = new GoogleAuthenticator(this.gacb.build());

    @POST
    @Path("/register")
    public Response register (@MultipartForm RegisterForm form) {

        if (!Validator.validateUsername(form.username)) {
            return ErrorMessage.USER_INVALID_USERNAME.respond();
        }

        if (!Validator.validateEmail(form.email)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        if (!Validator.validatePassword(form.password)) {
            return ErrorMessage.USER_INVALID_PASSWORD.respond();
        }

        if (PasswordUtility.isCompromised(form.password)) {
            return ErrorMessage.USER_COMPROMISED_PASSWORD.respond();
        }

        if (!form.terms) {
            return ErrorMessage.USER_INVALID_TERMS.respond();
        }

        final String email = form.email.toLowerCase();
        final String username = form.username.toLowerCase();

        final String domain = email.split("@")[1];
        if (DATABASE.securityDAO.existsBlocklist(email, domain)) {
            return ErrorMessage.USER_BLOCKLISTED_EMAIL.respond();
        }

        if (DATABASE.userDAO.existsUserByUsername(username) || DATABASE.userDAO.existsTempUserByUsername(username)) {
            return ErrorMessage.USER_TAKEN_USERNAME.respond();
        }
        if (DATABASE.userDAO.existsUserByEmail(email) || DATABASE.userDAO.existsTempUserByEmail(email)) {
            return ErrorMessage.USER_TAKEN_EMAIL.respond();
        }

        final String bcryptPassword = PasswordUtility.generateBCrypt(form.password);
        if (bcryptPassword == null) {
            return ErrorMessage.ERROR_ALGORITHM.respond();
        }

        final String verificationCode = UUID.randomUUID().toString();
        if (!DATABASE.userDAO.insertTempUser(email, username, bcryptPassword, "bcrypt", verificationCode)) {
            return ErrorMessage.FAILED_CREATE_TEMP_USER.respond();
        }

        if (Constants.POSTMARK_API_TOKEN != null) {
            final MessageResponse emailResponse = EmailUtil.sendVerificationEmail(email, verificationCode);
            if (emailResponse == null) {
                return ErrorMessage.FAILED_SEND_EMAIL.respond();
            }

            if (!DATABASE.securityDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                return ErrorMessage.FAILED_CREATE_EMAIL_SEND.respond();
            }
        }
        return ResponseUtil.successResponse(null);
    }


    @POST
    @Path("/verify")
    public Response verify (@MultipartForm VerifyForm form) {

        if (!Validator.validateEmail(form.email)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        if (GenericValidator.isBlankOrNull(form.code)) {
            return ErrorMessage.USER_INVALID_VERIFICATION_CODE.respond();
        }

        final String email = form.email.toLowerCase();

        if (DATABASE.userDAO.existsUserByEmail(email)) {
            return ErrorMessage.USER_VERIFIED.respond();
        }

        final TempUserRecord record = DATABASE.userDAO.findTempUserByEmailAndCode(email, form.code);
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

        if (!DATABASE.userDAO.insertUser(record.getEmail(), record.getUsername(), record.getUsername(), record.getPassword(), record.getPasswordType(), new Timestamp(record.getCreatedAt()))) {
            return ErrorMessage.FAILED_CREATE_USER.respond();
        }

        if (!DATABASE.userDAO.deleteTempUser(record.getEmail(), record.getUsername())) {
            return ErrorMessage.FAILED_DELETE_TEMP_USER.respond();
        }

        return ResponseUtil.successResponse(null);
    }

    @POST
    @Path("/request-reset-password")
    public Response requestResetPassword (@MultipartForm RequestResetPasswordForm form) {

        final String formEmail = form.email.toLowerCase();
        final String formUsername = form.username.toLowerCase();

        UserRecord record = DATABASE.userDAO.findOneByUsername(formUsername);

        if (record == null) {
            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        if (!record.getEmail().equalsIgnoreCase(formEmail)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        final String code = UUID.randomUUID().toString();
        if (!DATABASE.userDAO.insertPasswordReset(record.getId(), code)) {
            return ErrorMessage.FAILED_CREATE_PASSWORD_RESET.respond();
        }

        if (Constants.POSTMARK_API_TOKEN != null) {
            final MessageResponse emailResponse = EmailUtil.sendPasswordReset(record.getEmail(), code);
            if (emailResponse == null) {
                return ErrorMessage.FAILED_SEND_EMAIL.respond();
            }

            if (!DATABASE.securityDAO.insertEmailSent(emailResponse.getMessageId(), record.getEmail(), "reset-password")) {
                return ErrorMessage.FAILED_CREATE_EMAIL_SEND.respond();
            }
        }

        return ResponseUtil.successResponse(null);
    }

    @POST
    @Path("/reset-password")
    public Response resetPassword (@MultipartForm ResetPasswordForm form) {

        final String email = form.email.toLowerCase();
        final String code = form.code;

        if (!Validator.validatePassword(form.password)) {
            return ErrorMessage.USER_INVALID_PASSWORD.respond();
        }

        PasswordResetRecord record = DATABASE.userDAO.findOnePasswordResetByEmailAndCode(email, code);

        if (record == null) {
            return ErrorMessage.NOT_FOUND_PASSWORD_RESET.respond();
        }

        if (PasswordUtility.isCompromised(form.password)) {
            return ErrorMessage.USER_COMPROMISED_PASSWORD.respond();
        }

        final String bcryptPassword = PasswordUtility.generateBCrypt(form.password);
        if (bcryptPassword == null) {
            return ErrorMessage.ERROR_ALGORITHM.respond();
        }

        if (!DATABASE.userDAO.deletePasswordReset(record.getUserId(), code)) {
            return ErrorMessage.FAILED_DELETE_PASSWORD_RESET.respond();
        }

        if (System.currentTimeMillis() - record.getCreatedAt() >= TimeUnit.HOURS.toMillis(1)) {
            return ErrorMessage.PASSWORD_RESET_EXPIRED.respond();
        }

        if (!DATABASE.userDAO.updateUserPasswordByUserId(record.getUserId(), bcryptPassword)) {
            return ErrorMessage.FAILED_UPDATE_USER.respond();
        }

        if (!DATABASE.userDAO.deleteAllRefreshTokensByUserId(record.getUserId())) {
            return ErrorMessage.FAILED_DELETE_REFRESH_TOKEN.respond();
        }

        return ResponseUtil.successResponse(null);
    }

    @POST
    @Path("/resend")
    public Response resend (@MultipartForm ResendForm form) {

        if (!Validator.validateEmail(form.email)) {
            return ErrorMessage.USER_INVALID_EMAIL.respond();
        }

        if (!Validator.validateUsername(form.username)) {
            return ErrorMessage.USER_INVALID_USERNAME.respond();
        }

        final String tempEmail = form.email.toLowerCase();
        final String username = form.username.toLowerCase();

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

        final EmailSendRecord emailSendRecord = DATABASE.securityDAO.findEmailSentByEmailAndType(email, "verficiation");
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

            if (!DATABASE.securityDAO.insertEmailSent(emailResponse.getMessageId(), email, "verification")) {
                return ErrorMessage.FAILED_CREATE_EMAIL_SEND.respond();
            }
        }
        return ResponseUtil.successResponse(null);
    }

    @GET
    @Path("/checkusername/{username}")
    public Response checkUsername (@PathParam("username") String username) {

        if (DATABASE.userDAO.existsUserByUsername(username) || DATABASE.userDAO.existsTempUserByUsername(username)) {
            return ErrorMessage.USER_TAKEN_USERNAME.respond();
        }

        return ResponseUtil.successResponse(null);
    }
}