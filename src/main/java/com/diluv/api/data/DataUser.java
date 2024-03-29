package com.diluv.api.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.UsersEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents data about a user.
 */
public class DataUser {

    /**
     * The id of the user
     */
    @Expose
    private final long userId;

    /**
     * The username of the user.
     */
    @Expose
    private final String username;

    /**
     * The display name of the user.
     */
    @Expose
    private final String displayName;

    /**
     * A URL that points to their avatar image.
     */
    @Expose
    private final DataImage avatar;

    /**
     * The date the user created their account.
     */
    @Expose
    private final String createdAt;

    public DataUser (UsersEntity userRecord) {

        this.userId = userRecord.getId();
        this.username = userRecord.getUsername();
        this.displayName = userRecord.getDisplayName();
        this.avatar = Constants.getUserAvatar(userRecord.getUsername());
        this.createdAt = userRecord.getCreatedAt().toString();
    }
}