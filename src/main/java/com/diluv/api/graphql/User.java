package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.UsersEntity;

public class User {

    private long id;
    private String username;
    private String displayName;

    public User (UsersEntity entity) {

        this.id = entity.getId();
        this.username = entity.getUsername();
        this.displayName = entity.getDisplayName();
    }
}
