package com.diluv.api.data;

import com.diluv.confluencia.database.record.APITokensEntity;
import com.google.gson.annotations.Expose;

public class DataAPIToken {

    @Expose
    private final long id;

    @Expose
    private final String name;

    @Expose
    private final long createdAt;

    @Expose
    private final long lastUsed;

    public DataAPIToken (APITokensEntity rs) {

        this.id = rs.getId();
        this.name = rs.getName();
        this.createdAt = rs.getCreatedAt().getTime();
        this.lastUsed = rs.getLastUsed().getTime();
    }
}
