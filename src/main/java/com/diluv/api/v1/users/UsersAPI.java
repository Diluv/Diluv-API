package com.diluv.api.v1.users;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.GenericValidator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataAuthorizedUser;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataUser;
import com.diluv.api.utils.AuthUtilities;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.ErrorToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.query.ProjectQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UserChangeEmail;
import com.diluv.confluencia.database.record.UserMfaRecoveryEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.KeyRepresentation;

@GZIP
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersAPI {

    private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
        .setNumberOfScratchCodes(8)
        .setCodeDigits(8)
        .setSecretBits(160)
        .setKeyRepresentation(KeyRepresentation.BASE64)
        .build();
    private GoogleAuthenticator gAuth = new GoogleAuthenticator(config);

    @GET
    @Path("/self")
    public Response getSelf (@HeaderParam("Authorization") Token token) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        return Confluencia.getTransaction(session -> {
            final UsersEntity userRecord = Confluencia.USER.findOneByUserId(session, token.getUserId());

            if (userRecord == null) {

                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            return ResponseUtil.successResponse(new DataAuthorizedUser(userRecord));
        });
    }

    @PATCH
    @Path("/self")
    public Response patchSelf (@HeaderParam("Authorization") Token token, @MultipartForm UserUpdateForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        return Confluencia.getTransaction(session -> {
            final UsersEntity user = Confluencia.USER.findOneByUserId(session, token.getUserId());

            if (user == null) {

                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            if (!OpenBSDBCrypt.checkPassword(user.getPassword(), form.currentPassword.toCharArray())) {

                return ErrorMessage.USER_INVALID_PASSWORD.respond();
            }

            if (!GenericValidator.isBlankOrNull(form.displayName)) {

                String displayName = form.displayName.trim();
                if (!displayName.equals(user.getDisplayName())) {

                    if (!Validator.validateUserDisplayName(user.getUsername(), displayName)) {

                        return ErrorMessage.USER_INVALID_DISPLAY_NAME.respond();
                    }
                    user.setDisplayName(displayName);
                }
            }

            if (!GenericValidator.isBlankOrNull(form.newPassword)) {

                String password = form.newPassword.trim();
                if (!Validator.validatePassword(password)) {

                    return ErrorMessage.USER_INVALID_NEW_PASSWORD.respond();
                }

                user.setPassword(AuthUtilities.getBcrypt(password.toCharArray()));
            }

            if (!GenericValidator.isBlankOrNull(form.email)) {

                String email = form.email.trim();
                if (!user.getEmail().equalsIgnoreCase(email)) {

                    if (!Validator.validateEmail(email)) {

                        return ErrorMessage.USER_INVALID_EMAIL.respond();
                    }

                    if (Confluencia.USER.existsByEmail(session, email)
                        || Confluencia.USER.existsTempUserByEmail(session, email)) {

                        return ErrorMessage.USER_TAKEN_EMAIL.respond();
                    }

                    if (Confluencia.USER.existUserChangeEmailByUser(session, user)) {
                        if (!Confluencia.USER.deleteUserChangeEmail(session, user)) {
                            // TODO ERROR Internally
                            return ErrorMessage.THROWABLE.respond();
                        }
                    }

                    UserChangeEmail userChangeEmail = new UserChangeEmail();
                    userChangeEmail.setUser(user);
                    userChangeEmail.setEmail(email);
                    userChangeEmail.setCode(AuthUtilities.getSecureRandomNumeric(8));

                    session.save(userChangeEmail);
                    //TODO Send email
                }
            }

            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PATCH
    @Path("/self/mfa")
    public Response patchSelfMFA (@HeaderParam("Authorization") Token token, @MultipartForm User2FAForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        return Confluencia.getTransaction(session -> {
            final UsersEntity user = Confluencia.USER.findOneByUserId(session, token.getUserId());

            if (user == null) {

                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            if (!OpenBSDBCrypt.checkPassword(user.getPassword(), form.password.toCharArray())) {
                return ErrorMessage.USER_INVALID_PASSWORD.respond();
            }

            if ("enable".equalsIgnoreCase(form.mfaStatus)) {
                if (!Validator.validateMFA(form.mfa)) {
                    return ErrorMessage.USER_INVALID_MFA.respond();
                }

                if (GenericValidator.isBlankOrNull(form.mfaSecret) || !Base64.isBase64(form.mfaSecret)) {
                    return ErrorMessage.USER_INVALID_MFA_SECRET.respond();
                }

                if (!gAuth.authorize(form.mfaSecret, form.mfa)) {
                    return ErrorMessage.USER_INVALID_MFA_AND_MFA_SECRET.respond();
                }

                Set<String> mfaScratch = new HashSet<>();
                for (int i = 0; i < 8; i++) {
                    String code;
                    do {
                        code = AuthUtilities.getSecureRandomAlphanumeric(8);
                    } while (mfaScratch.contains(code));
                    mfaScratch.add(code);
                    session.save(new UserMfaRecoveryEntity(user, code));
                }

                user.setMfaSecret(form.mfaSecret);
                user.setMfa(true);

                session.update(user);
            }
            else if ("disable".equalsIgnoreCase(form.mfaStatus)) {
                user.setMfa(false);
                user.setMfaSecret(null);

                if (!Confluencia.USER.deleteUserMFARecovery(session, user)) {
                    //return ErrorMessage.FAILED_DELETE_MFA_RECOVERY.respond();
                    return ErrorMessage.THROWABLE.respond();
                }

                session.update(user);
            }

            return ResponseUtil.successResponse(new DataAuthorizedUser(user));
        });
    }

    @GET
    @Path("/{username}")
    public Response getUser (@HeaderParam("Authorization") Token token, @PathParam("username") String username) {

        return Confluencia.getTransaction(session -> {
            final UsersEntity userRecord = Confluencia.USER.findOneByUsername(session, username);

            if (userRecord == null) {

                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            if (token != null && token.getUserId() == userRecord.getId()) {
                return ResponseUtil.successResponse(new DataAuthorizedUser(userRecord));
            }

            return ResponseUtil.successResponse(new DataUser(userRecord));
        });
    }

    @GET
    @Path("/{username}/projects")
    public Response getProjectsByUsername (@HeaderParam("Authorization") Token token,
                                           @PathParam("username") String username, @Query ProjectQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(ProjectSort.POPULAR);

        return Confluencia.getTransaction(session -> {
            UsersEntity userRecord = Confluencia.USER.findOneByUsername(session, username);
            if (userRecord == null) {
                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            boolean authorized = false;

            if (token != null) {
                authorized = userRecord.getUsername().equalsIgnoreCase(username);
            }

            List<ProjectsEntity> projects =
                Confluencia.PROJECT.findAllByUsername(session, username, authorized, page, limit, sort);
            return ResponseUtil.successResponse(projects.stream().map(DataProject::new).collect(Collectors.toList()));
        });
    }
}