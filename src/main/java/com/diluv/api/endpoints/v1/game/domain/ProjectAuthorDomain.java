package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;

public class ProjectAuthorDomain {
    private final String username;
    private final String role;

    public ProjectAuthorDomain (ProjectAuthorRecord projectAuthor) {

        this.username = projectAuthor.getUsername();
        this.role = projectAuthor.getRole();
    }

    public ProjectAuthorDomain (String username, String role) {

        this.username = username;
        this.role = role;
    }
}
