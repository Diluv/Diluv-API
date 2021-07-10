package com.diluv.api.utils.auth.tokens;

import java.util.List;

import com.diluv.api.utils.Constants;

public class Token {
    private final String token;
    private final long userId;
    private final boolean apiToken;
    private final List<String> globalProjectPermissions;

    public Token (String token, long userId, boolean apiToken, List<String> globalProjectPermissions) {

        this.token = token;
        this.userId = userId;
        this.apiToken = apiToken;
        this.globalProjectPermissions = globalProjectPermissions;
    }

    public String getToken () {

        return this.token;
    }

    public long getUserId () {

        return this.userId;
    }

    public boolean isApiToken () {

        if (Constants.isDevelopment()) {
            return false;
        }
        return this.apiToken;
    }

    public List<String> getGlobalProjectPermissions () {

        return this.globalProjectPermissions;
    }
}