package com.diluv.api.endpoints.v1.user;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.UserRecord;

/**
 * Represents data about a user.
 */
public class DataUser {
    
    /**
     * The username of the user.
     */
    private final String username;
    
    /**
     * A URL that points to their avatar image.
     */
    private final String avatarURL;
    
    /**
     * The date the user created their account.
     */
    private final long createdAt;
    
    public DataUser(UserRecord userRecord) {
        
        this.username = userRecord.getUsername();
        this.avatarURL = Constants.getUserAvatar(userRecord.getUsername());
        this.createdAt = userRecord.getCreatedAt();
    }
    
    public String getUsername () {
        
        return this.username;
    }
    
    public String getAvatarURL () {
        
        return this.avatarURL;
    }
    
    public long getCreatedAt () {
        
        return this.createdAt;
    }
}
