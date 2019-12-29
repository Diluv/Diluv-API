package com.diluv.api.endpoints.v1.user.domain;

import com.diluv.api.database.record.UserRecord;

public class UserDomain {
    private final String username;

    public UserDomain (UserRecord userRecord) {

        this.username = userRecord.getUsername();
    }

    public String getUsername () {

        return username;
    }
}
