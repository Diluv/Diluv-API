package com.diluv.api.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.UserRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents data about a user.
 */
public class DataUser {
    
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
    
    public DataUser(UserRecord userRecord) {
        
        this.username = userRecord.getUsername();
        this.avatarURL = Constants.getUserAvatar(userRecord.getUsername());
        this.createdAt = userRecord.getCreatedAt();
    }
}