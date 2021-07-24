package com.diluv.api.data;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.database.record.APITokensEntity;
import com.google.gson.annotations.Expose;

import java.util.List;

public class DataUserAPIToken extends DataUserToken {

    @Expose
    private final String name;

    @Expose
    private final String createdAt;

    @Expose
    private final String lastUsed;

    public DataUserAPIToken (Token token, APITokensEntity apiToken) {

        super(apiToken.getUser(), token);
        this.name = apiToken.getName();
        this.createdAt = apiToken.getCreatedAt().toString();
        this.lastUsed = apiToken.getLastUsed().toString();
    }

    public DataUserAPIToken (APITokensEntity apiToken, List<String> permissions) {

        super(apiToken.getUser(), permissions);
        this.name = apiToken.getName();
        this.createdAt = apiToken.getCreatedAt().toString();
        this.lastUsed = apiToken.getLastUsed().toString();
    }
}
