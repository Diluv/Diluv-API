package com.diluv.api.data;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.database.record.APITokensEntity;
import com.google.gson.annotations.Expose;

public class DataUserAPIToken extends DataUserToken {

    @Expose
    private final long tokenId;

    @Expose
    private final String name;

    @Expose
    private final String createdAt;

    @Expose
    private final String lastUsed;

    public DataUserAPIToken (Token token, APITokensEntity apiToken) {

        super(apiToken.getUser(), token);
        this.tokenId = apiToken.getId();
        this.name = apiToken.getName();
        this.createdAt = apiToken.getCreatedAt().toString();
        this.lastUsed = apiToken.getLastUsed().toString();
    }
}
