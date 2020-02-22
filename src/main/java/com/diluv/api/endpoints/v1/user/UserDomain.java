package com.diluv.api.endpoints.v1.user;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.UserRecord;

public class UserDomain {
    private final String username;
    private final String avatarURL;
    private final long createdAt;
    
    public UserDomain(UserRecord userRecord) {
        
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
