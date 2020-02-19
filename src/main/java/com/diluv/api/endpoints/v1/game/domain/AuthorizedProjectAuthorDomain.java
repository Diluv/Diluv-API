package com.diluv.api.endpoints.v1.game.domain;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;

public class AuthorizedProjectAuthorDomain extends ProjectAuthorDomain {
    private final List<String> permissions;

    public AuthorizedProjectAuthorDomain (ProjectAuthorRecord projectAuthor) {

        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions();
    }

    public List<String> getPermissions () {

        return this.permissions;
    }
}
