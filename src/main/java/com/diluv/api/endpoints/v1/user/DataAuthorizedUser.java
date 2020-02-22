package com.diluv.api.endpoints.v1.user;

import com.diluv.confluencia.database.record.UserRecord;

public class DataAuthorizedUser extends DataUser {
    private final String email;
    private final boolean mfa;
    
    public DataAuthorizedUser(UserRecord userRecord) {
        
        super(userRecord);
        
        this.email = userRecord.getEmail();
        this.mfa = userRecord.isMfa();
    }
    
    public String getEmail () {
        
        return this.email;
    }
    
    public boolean isMfa () {
        
        return this.mfa;
    }
}
