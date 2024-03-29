package com.diluv.api.utils.error;

import javax.ws.rs.core.Response;

import com.diluv.api.utils.response.ErrorResponse;

import static com.diluv.api.utils.error.ErrorType.*;

public enum ErrorMessage {

    // User Errors
    USER_INVALID_TOKEN(UNAUTHORIZED, "invalid_token", "The token is invalid."),
    USER_INVALID_API_TOKEN(UNAUTHORIZED, "invalid_token", "Can't use an API token for this request"),

    USER_REQUIRED_TOKEN(UNAUTHORIZED, "required_token", "A token is required for this request."),
    USER_NOT_AUTHORIZED(UNAUTHORIZED, "user_unauthorized", "The user is not authorized to make this request."),
    USER_INVALID_DISPLAY_NAME(BAD_REQUEST, "invalid_display_name", "The display name for the user is not valid. Can only change the case of the username"),
    USER_INVALID_PASSWORD(BAD_REQUEST, "invalid_password", "The password is invalid, must match the current password."),
    USER_INVALID_NEW_PASSWORD(BAD_REQUEST, "invalid_new_password", "The password is invalid, password must be between 8 and 70 characters."),
    USER_INVALID_EMAIL(BAD_REQUEST, "invalid_email", "The email is not valid."),
    USER_INVALID_MFA(BAD_REQUEST, "invalid_mfa", "MFA is required for the request."),
    USER_INVALID_MFA_SECRET(BAD_REQUEST, "invalid_mfa_secret", "The MFA secret is required, and must be base64."),
    USER_INVALID_MFA_AND_MFA_SECRET(BAD_REQUEST, "invalid_mfa_and_mfa_secret", "The MFA is invalid for."),
    USER_TAKEN_EMAIL(BAD_REQUEST, "taken_email", "The email is already taken."),

    // Project Errors
    PROJECT_INVALID_NAME(BAD_REQUEST, "invalid_name", "The name is not valid"),
    INVALID_IMAGE(BAD_REQUEST, "invalid_image", "The image is not valid, please verify you are sending an image."),
    PROJECT_INVALID_TAGS(BAD_REQUEST, "invalid_tags", null),
    PROJECT_INVALID_SUMMARY(BAD_REQUEST, "invalid_summary", "The summary must be less then 250 characters"),
    PROJECT_INVALID_DESCRIPTION(BAD_REQUEST, "invalid_description", "The description must be greater then 50 characters but less then 10000."),
    PROJECT_TAKEN_SLUG(BAD_REQUEST, "project.taken.slug", "Project slug taken"),
    PROJECT_NOT_MEMBER(BAD_REQUEST, "project.not_member", "The user is not a member of the project."),
    PROJECT_OWNER(BAD_REQUEST, "project.owner", "The user is the owner of the project."),
    PROJECT_PENDING_INVITE(BAD_REQUEST, "project.pending_invite", "The user already has a pending invite for this project."),

    PROJECT_FILE_INVALID_DEPEND_SELF(BAD_REQUEST, "invalid_depend_self", "The file can't depend on itself."),
    PROJECT_FILE_INVALID_DEPENDENCY_ID(BAD_REQUEST, "invalid_dependency_id", null),
    PROJECT_FILE_INVALID_DEPENDENCY_TYPE(BAD_REQUEST, "invalid_dependency_type", "Dependency type must be required, optional or incompatible"),
    PROJECT_FILE_INVALID_CHANGELOG(BAD_REQUEST, "invalid_changelog", "The changelog is not valid, must be null or below 2000 characters."),
    PROJECT_FILE_INVALID_FILENAME(BAD_REQUEST, "invalid_filename", "The file name is invalid, must be allowed by the project type"),
    PROJECT_FILE_INVALID_DISPLAY_NAME(BAD_REQUEST, "invalid_display_name", "The display name is invalid."),
    PROJECT_FILE_INVALID_FILE(BAD_REQUEST, "invalid_file", "File is invalid, cannot be null"),
    PROJECT_FILE_INVALID_RELEASE_TYPE(BAD_REQUEST, "invalid_release_type", "Must be release, beta or alpha"),
    PROJECT_FILE_INVALID_CLASSIFIER(BAD_REQUEST, "invalid_classifier", "Must be binary"),
    PROJECT_FILE_INVALID_VERSION(BAD_REQUEST, "invalid_version", "Must specify a version that is at most 20 characters."),
    PROJECT_FILE_INVALID_GAME_VERSION(BAD_REQUEST, "invalid_game_version", null),
    PROJECT_FILE_INVALID_LOADER(BAD_REQUEST, "invalid_loader", null),
    PROJECT_FILE_TAKEN_VERSION(BAD_REQUEST, "project_file.taken.version", "The file version is taken, must be unique"),

    INVALID_PROJECT_INVITE_STATUS(BAD_REQUEST, "invalid.project_invite.status", ""),

    NOT_FOUND_GAME(BAD_REQUEST, "notfound.game", "The game was the not found."),
    NOT_FOUND_PROJECT(BAD_REQUEST, "notfound.project", "The project was not found."),
    NOT_FOUND_PROJECT_FILE(BAD_REQUEST, "notfound.project_file", "The project file was not found."),
    NOT_FOUND_PROJECT_TYPE(BAD_REQUEST, "notfound.project_type", "The project type was not found."),
    NOT_FOUND_USER(BAD_REQUEST, "notfound.user", "The user was not found."),
    NOT_FOUND_NEWS(BAD_REQUEST, "notfound.news", "The news post was not found."),
    NOT_FOUND_NOTIFICATION(BAD_REQUEST, "notfound.notification", "The notification was not found."),
    NOT_FOUND_INVITE(BAD_REQUEST, "notfound.invite", "The invite was not found."),

    REQUIRES_IMAGE(BAD_REQUEST, "requires_image", "The image is required"),

    INVALID_DATA(BAD_REQUEST, "invalid_data", "The data is invalid."),
    INVALID_ROLE(BAD_REQUEST, "invalid_role", "The role is not valid"),
    INVALID_PERMISSION(BAD_REQUEST, "invalid_permission", "The permissions are not valid"),

    TOKEN_INVALID_PERMISSIONS(BAD_REQUEST, "token_invalid_permissions", "The token has invalid permissions."),
    TOKEN_INVALID_NAME(BAD_REQUEST, "token_invalid_name", "The token has an invalid name."),
    TOKEN_INVALID_ID(BAD_REQUEST, "token_invalid_id", "The id is invalid."),

    FILE_NOT_FOUND(NOT_FOUND, "file.404", "The file was not found."),

    ENDPOINT_NOT_FOUND(NOT_FOUND, "generic.404", "The endpoint was not found."),
    THROWABLE(INTERNAL_SERVER_ERROR, "generic.throwable", "The request had an error, please try again later.");

    private final ErrorType type;
    private final String uniqueId;
    private final String message;

    ErrorMessage (ErrorType type, String uniqueId, String message) {

        this.type = type;
        this.uniqueId = uniqueId;
        this.message = message;
    }

    public ErrorType getType () {

        return this.type;
    }

    public String getUniqueId () {

        return this.uniqueId;
    }

    public String getMessage () {

        return this.message;
    }

    public final Response respond (String message) {

        return Response.status(this.type.code).entity(new ErrorResponse(this.type.getError(), this.uniqueId, message)).build();
    }

    public final Response respond () {

        return Response.status(this.type.code).entity(new ErrorResponse(this.type.getError(), this.uniqueId, this.message)).build();
    }
}
