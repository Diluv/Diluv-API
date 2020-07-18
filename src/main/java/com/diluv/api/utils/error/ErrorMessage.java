package com.diluv.api.utils.error;

import javax.ws.rs.core.Response;

import com.diluv.api.utils.response.ErrorResponse;

public enum ErrorMessage {

    // User Errors
    USER_INVALID_TERMS(ErrorType.BAD_REQUEST, "user.invalid.terms"),
    USER_INVALID_EMAIL(ErrorType.BAD_REQUEST, "user.invalid.email"),
    USER_INVALID_TOKEN(ErrorType.UNAUTHORIZED, "user.invalid.token"),
    USER_INVALID_USERNAME(ErrorType.BAD_REQUEST, "user.invalid.username"),
    USER_INVALID_PASSWORD(ErrorType.BAD_REQUEST, "user.invalid.password"),
    USER_INVALID_VERIFICATION_CODE(ErrorType.BAD_REQUEST, "user.invalid.verification_code"),
    USER_REQUIRED_TOKEN(ErrorType.UNAUTHORIZED, "user.required.token"),
    USER_NOT_AUTHORIZED(ErrorType.UNAUTHORIZED, "user.unauthorized"),
    USER_COMPROMISED_PASSWORD(ErrorType.BAD_REQUEST, "user.compromised.password"),

    USER_TAKEN_EMAIL(ErrorType.BAD_REQUEST, "user.taken.email"),
    USER_TAKEN_USERNAME(ErrorType.BAD_REQUEST, "user.taken.username"),
    USER_VERIFIED(ErrorType.BAD_REQUEST, "user.verified"),
    USER_BLOCKLISTED_EMAIL(ErrorType.BAD_REQUEST, "user.blocklisted.email"),

    // Project Errors
    PROJECT_INVALID_NAME(ErrorType.BAD_REQUEST, "project.invalid.name"),
    PROJECT_INVALID_LOGO(ErrorType.BAD_REQUEST, "project.invalid.logo"),
    PROJECT_INVALID_TAGS(ErrorType.BAD_REQUEST, "project.invalid.tags"),
    PROJECT_INVALID_SUMMARY(ErrorType.BAD_REQUEST, "project.invalid.summary"),
    PROJECT_INVALID_DESCRIPTION(ErrorType.BAD_REQUEST, "project.invalid.description"),
    PROJECT_TAKEN_SLUG(ErrorType.BAD_REQUEST, "project.taken.slug"),

    PROJECT_FILE_INVALID_SAME_ID(ErrorType.BAD_REQUEST, "project_file.invalid.same_id"),
    PROJECT_FILE_INVALID_DEPENDENCY_ID(ErrorType.BAD_REQUEST, "project_file.invalid.dependency_id"),
    PROJECT_FILE_INVALID_PROJECT_ID(ErrorType.BAD_REQUEST, "project_file.invalid.project_id"),
    PROJECT_FILE_INVALID_CHANGELOG(ErrorType.BAD_REQUEST, "project_file.invalid.changelog"),
    PROJECT_FILE_INVALID_FILENAME(ErrorType.BAD_REQUEST, "prject_file.invalid.filename"),
    PROJECT_FILE_INVALID_FILE(ErrorType.BAD_REQUEST, "project_file.invalid.file"),
    PROJECT_FILE_INVALID_RELEASE_TYPE(ErrorType.BAD_REQUEST, "project_file.invalid.release_type"),
    PROJECT_FILE_INVALID_CLASSIFIER(ErrorType.BAD_REQUEST, "project_file.invalid.classifier"),
    PROJECT_FILE_INVALID_VERSION(ErrorType.BAD_REQUEST, "project_file.invalid.version"),
    PROJECT_FILE_INVALID_GAME_VERSION(ErrorType.BAD_REQUEST, "project_file.invalid.game_version"),
    PROJECT_FILE_TAKEN_VERSION(ErrorType.BAD_REQUEST, "project_file.taken.version"),

    PASSWORD_RESET_EXPIRED(ErrorType.BAD_REQUEST, "password_reset.expired"),

    FAILED_CREATE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_temp_user"),
    FAILED_CREATE_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_user"),
    FAILED_CREATE_PROJECT(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project"),
    FAILED_UPDATE_PROJECT(ErrorType.INTERNAL_SERVER_ERROR, "database.update_project"),
    FAILED_CREATE_PROJECT_TAGS(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project_tags"),
    FAILED_CREATE_PROJECT_FILE(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project_file"),
    FAILED_CREATE_PASSWORD_RESET(ErrorType.INTERNAL_SERVER_ERROR, "database.create_password_reset"),

    FAILED_CREATE_PROJECT_FILE_GAME_VERSION(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project_file_game_version"),

    FAILED_UPDATE_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.update_user"),
    FAILED_DELETE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_temp_user"),
    FAILED_DELETE_REFRESH_TOKEN(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_refresh_token"),
    FAILED_DELETE_PASSWORD_RESET(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_password_reset"),
    FAILED_SHA512(ErrorType.INTERNAL_SERVER_ERROR, "failed.sha512"),

    FAILED_CREATE_EMAIL_SEND(ErrorType.INTERNAL_SERVER_ERROR, "database.create_email_sent"),
    FAILED_SEND_EMAIL(ErrorType.INTERNAL_SERVER_ERROR, "email.send"),
    FAILED_TEMP_FILE(ErrorType.INTERNAL_SERVER_ERROR, "error.temp_file"),

    NOT_FOUND_GAME(ErrorType.BAD_REQUEST, "notfound.game"),
    NOT_FOUND_PROJECT_TYPE(ErrorType.BAD_REQUEST, "notfound.project_type"),
    NOT_FOUND_USER(ErrorType.BAD_REQUEST, "notfound.user"),
    NOT_FOUND_PROJECT(ErrorType.BAD_REQUEST, "notfound.project"),
    NOT_FOUND_USER_REFRESH_TOKEN(ErrorType.BAD_REQUEST, "notfound.user_refresh_token"),
    NOT_FOUND_NEWS(ErrorType.BAD_REQUEST, "notfound.news"),
    NOT_FOUND_PASSWORD_RESET(ErrorType.BAD_REQUEST, "notfound.password_reset"),

    ERROR_TOKEN(ErrorType.BAD_REQUEST, "error.token"),
    ERROR_WRITING(ErrorType.INTERNAL_SERVER_ERROR, "error.writing"),
    ERROR_ALGORITHM(ErrorType.INTERNAL_SERVER_ERROR, "error.algorithm"),
    ERROR_SAVING_IMAGE(ErrorType.INTERNAL_SERVER_ERROR, "error.saving_image"),

    COOLDOWN_EMAIL(ErrorType.BAD_REQUEST, "cooldown.email"),

    NOT_FOUND(ErrorType.BAD_REQUEST, "generic.404"),
    THROWABLE(ErrorType.INTERNAL_SERVER_ERROR, "generic.throwable");

    private final ErrorType type;
    private final String message;

    ErrorMessage (ErrorType type, String message) {

        this.type = type;
        this.message = message;
    }

    public ErrorType getType () {

        return this.type;
    }

    public String getMessage () {

        return this.message;
    }

    public Response respond () {

        return Response.status(this.type.code).entity(new ErrorResponse(this.getType().getError(), this.getMessage())).build();
    }
}
