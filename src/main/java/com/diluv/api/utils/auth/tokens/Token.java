package com.diluv.api.utils.auth.tokens;

import java.util.List;

public class Token {
    private final long userId;
    private final boolean apiToken;
    private final List<String> globalProjectPermissions;

    public Token (long userId, boolean apiToken, List<String> globalProjectPermissions) {

        this.userId = userId;
        this.apiToken = apiToken;
        this.globalProjectPermissions = globalProjectPermissions;
    }

    public long getUserId () {

        return this.userId;
    }

    public boolean isApiToken () {

        return this.apiToken;
    }

    public List<String> getGlobalProjectPermissions () {

        return this.globalProjectPermissions;
    }
}