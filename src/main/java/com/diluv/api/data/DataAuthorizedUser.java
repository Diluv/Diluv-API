package com.diluv.api.data;

import com.diluv.confluencia.database.record.UserRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents data on a user from an authorized perspective.
 */
public class DataAuthorizedUser extends DataUser {
    
    /**
     * The email address of the user.
     */
    @Expose
    private final String email;
    
    /**
     * Whether or not the account has MFA enabled.
     */
    @Expose
    private final boolean mfa;
    
    public DataAuthorizedUser(UserRecord userRecord) {
        
        super(userRecord);
        
        this.email = userRecord.getEmail();
        this.mfa = userRecord.isMfa();
    }
}