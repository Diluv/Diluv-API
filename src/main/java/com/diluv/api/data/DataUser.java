package com.diluv.api.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.UserRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents data about a user.
 */
public class DataUser {

    /**
     * The id of the user
     */
    private final long userId;

    /**
     * The username of the user.
     */
    @Expose
    private final String username;

    /**
     * A URL that points to their avatar image.
     */
    @Expose
    private final String avatarURL;

    /**
     * The date the user created their account.
     */
    @Expose
    private final long createdAt;

    public DataUser (UserRecord userRecord) {

        this.userId = userRecord.getId();
        this.username = userRecord.getUsername();
        this.avatarURL = Constants.getUserAvatar(userRecord.getUsername());
        this.createdAt = userRecord.getCreatedAt();
    }

    public DataUser (long userId, String username, long createdAt) {

        this.userId = userId;
        this.username = username;
        this.avatarURL = Constants.getUserAvatar(username);
        this.createdAt = createdAt;
    }
}