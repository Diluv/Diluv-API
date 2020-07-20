package com.diluv.api.utils.auth.tokens;

import java.util.List;

public class Token {
    private final long userId;
    private final List<String> globalProjectPermissions;

    public Token (long userId, List<String> globalProjectPermissions) {

        this.userId = userId;
        this.globalProjectPermissions = globalProjectPermissions;
    }

    public long getUserId () {

        return this.userId;
    }

    public List<String> getGlobalProjectPermissions () {

        return this.globalProjectPermissions;
    }
}