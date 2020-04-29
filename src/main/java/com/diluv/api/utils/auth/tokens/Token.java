package com.diluv.api.utils.auth.tokens;

import java.util.List;

public class Token {
    private final long userId;
    private final List<String> projectPermissions;

    public Token (long userId, List<String> projectPermissions) {

        this.userId = userId;
        this.projectPermissions = projectPermissions;
    }

    public long getUserId () {

        return this.userId;
    }

    public List<String> getProjectPermissions () {

        return this.projectPermissions;
    }
}