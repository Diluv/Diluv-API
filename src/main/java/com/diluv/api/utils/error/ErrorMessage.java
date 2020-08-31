package com.diluv.api.utils.error;

import com.diluv.api.utils.response.ErrorResponse;

import javax.ws.rs.core.Response;

import static com.diluv.api.utils.error.ErrorType.*;

public enum ErrorMessage {

    // User Errors
    USER_INVALID_TOKEN(UNAUTHORIZED, "invalid_token", "The token is invalid."),
    USER_REQUIRED_TOKEN(UNAUTHORIZED, "required_token", "A token is required for this request."),
    USER_NOT_AUTHORIZED(UNAUTHORIZED, "user_unauthorized", "The user is not authorized to make this request."),
    USER_INVALID_DISPLAY_NAME(BAD_REQUEST, "invalid_display_name", "The display name for the user is not valid. Can only change the case of the username"),
    USER_INVALID_PASSWORD(BAD_REQUEST, "invalid_password", "The password is invalid, must match the current password."),
    USER_INVALID_NEW_PASSWORD(BAD_REQUEST, "invalid_new_password", "The password is invalid, password must be between 8 and 70 characters."),
    USER_INVALID_MFA(BAD_REQUEST, "invalid_mfa", "MFA is required for the request."),
    USER_INVALID_MFA_SECRET(BAD_REQUEST, "invalid_mfa_secret", "The MFA secret is required, and must be base64."),
    USER_INVALID_MFA_AND_MFA_SECRET(BAD_REQUEST, "invalid_mfa_and_mfa_secret", "The MFA is invalid for."),

    // Project Errors
    PROJECT_INVALID_NAME(BAD_REQUEST, "invalid_name", "The name is not valid"),
    PROJECT_INVALID_LOGO(BAD_REQUEST, "invalid_logo", "The logo is not valid, please verify you are sending an image."),
    PROJECT_INVALID_TAGS(BAD_REQUEST, "invalid_tags", null),
    PROJECT_INVALID_SUMMARY(BAD_REQUEST, "invalid_summary", "The summary must be less then 250 characters"),
    PROJECT_INVALID_DESCRIPTION(BAD_REQUEST, "invalid_description", "The description must be greater then 50 characters but less then 10000."),
    PROJECT_TAKEN_SLUG(BAD_REQUEST, "project.taken.slug", ""),

    PROJECT_FILE_INVALID_DEPEND_SELF(BAD_REQUEST, "invalid_depend_self", "The file can't depend on itself."),
    PROJECT_FILE_INVALID_DEPENDENCY_ID(BAD_REQUEST, "invalid_dependency_id", null),
    PROJECT_FILE_INVALID_CHANGELOG(BAD_REQUEST, "invalid_changelog", "The changelog is not valid, must be null or below 2000 characters."),
    PROJECT_FILE_INVALID_FILENAME(BAD_REQUEST, "invalid_filename", "The file name is invalid, must be allowed by the project type"),
    PROJECT_FILE_INVALID_FILE(BAD_REQUEST, "invalid_file", "File is invalid, cannot be null"),
    PROJECT_FILE_INVALID_RELEASE_TYPE(BAD_REQUEST, "invalid_release_type", "Must be release, beta or alpha"),
    PROJECT_FILE_INVALID_CLASSIFIER(BAD_REQUEST, "invalid_classifier", "Must be binary"),
    PROJECT_FILE_INVALID_VERSION(BAD_REQUEST, "invalid_version", "Must specify a semver version at most 20 characters."),
    PROJECT_FILE_INVALID_GAME_VERSION(BAD_REQUEST, "invalid_game_version", null),
    PROJECT_FILE_TAKEN_VERSION(BAD_REQUEST, "project_file.taken.version", "The file version is taken, must be unique"),

    NOT_FOUND_GAME(BAD_REQUEST, "notfound.game", "The game was the not found."),
    NOT_FOUND_PROJECT(BAD_REQUEST, "notfound.project", "The project was not found."),
    NOT_FOUND_PROJECT_FILE(BAD_REQUEST, "notfound.project_file", "The project file was not found."),
    NOT_FOUND_PROJECT_TYPE(BAD_REQUEST, "notfound.project_type", "The project type was not found."),
    NOT_FOUND_USER(BAD_REQUEST, "notfound.user", "The user was not found."),
    NOT_FOUND_NEWS(BAD_REQUEST, "notfound.news", "The news post was not found"),

    INVALID_DATA(BAD_REQUEST, "invalid_data", "The data is invalid."),

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
