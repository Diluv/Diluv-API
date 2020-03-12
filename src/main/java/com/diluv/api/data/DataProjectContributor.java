package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a user who contributed to a project.
 */
public class DataProjectContributor extends DataUser {

    /**
     * The role the contributor played in the creation of the project.
     */
    @Expose
    private final String role;

    public DataProjectContributor (ProjectAuthorRecord projectAuthor) {

        super(projectAuthor.getUserId(), projectAuthor.getUsername(), projectAuthor.getDisplayName(), projectAuthor.getCreatedAt());
        this.role = projectAuthor.getRole();
    }

    public DataProjectContributor (long userId, String username, String displayName, long createdAt, String role) {

        super(userId, username, displayName, createdAt);
        this.role = role;
    }
}
