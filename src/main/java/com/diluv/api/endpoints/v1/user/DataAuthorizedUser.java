package com.diluv.api.endpoints.v1.user;

import com.diluv.confluencia.database.record.UserRecord;
import com.google.gson.annotations.Expose;

public class DataAuthorizedUser extends DataUser {
    
    @Expose
    private final String email;
    
    @Expose
    private final boolean mfa;
    
    public DataAuthorizedUser(UserRecord userRecord) {
        
        super(userRecord);
        
        this.email = userRecord.getEmail();
        this.mfa = userRecord.isMfa();
    }
}