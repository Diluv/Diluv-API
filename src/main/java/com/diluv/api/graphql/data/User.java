package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.UsersEntity;

public class User {

    private long id;
    private String username;
    private String displayName;
    private long createdAt;

    public User (UsersEntity entity) {

        this.id = entity.getId();
        this.username = entity.getUsername();
        this.displayName = entity.getDisplayName();
        this.createdAt = entity.getCreatedAt().getTime();
    }
}
