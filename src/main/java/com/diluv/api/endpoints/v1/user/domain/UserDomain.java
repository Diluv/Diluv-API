package com.diluv.api.endpoints.v1.user.domain;

import com.diluv.confluencia.database.record.UserRecord;

public class UserDomain {
    private final String username;
    private final long createdAt;

    public UserDomain (UserRecord userRecord) {

        this.username = userRecord.getUsername();
        this.createdAt = userRecord.getCreatedAt();
    }

    public String getUsername () {

        return this.username;
    }

    public long getCreatedAt () {

        return this.createdAt;
    }
}
