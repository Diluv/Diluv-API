package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
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

    public DataProjectContributor (ProjectAuthorsEntity author) {

        super(author.getUser());
        this.role = author.getRole();
    }

    public DataProjectContributor (UsersEntity user, String role) {

        super(user);
        this.role = role;
    }
}
