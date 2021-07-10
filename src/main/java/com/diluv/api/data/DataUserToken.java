package com.diluv.api.data;

import java.util.List;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.database.record.UsersEntity;
import com.google.gson.annotations.Expose;

public class DataUserToken {

    @Expose
    private final long userId;

    @Expose
    private final String username;

    @Expose
    private final String displayName;

    @Expose
    private final boolean apiToken;

    /**
     * The permissions this token can handle, e.g. API tokens can restrict actions to only file upload
     */
    @Expose
    private final List<String> permissions;

    public DataUserToken (UsersEntity user, Token token) {

        this.userId = user.getId();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.apiToken = token.isApiToken();

        this.permissions = token.getGlobalProjectPermissions();
    }
}
