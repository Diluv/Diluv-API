package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a user who are authors to a project.
 */
public class DataProjectAuthor extends DataUser {

    /**
     * The role the author played in the creation of the project.
     */
    @Expose
    private final String role;

    public DataProjectAuthor (ProjectAuthorsEntity author) {

        super(author.getUser());
        this.role = author.getRole();
    }

    public DataProjectAuthor (UsersEntity user, String role) {

        super(user);
        this.role = role;
    }
}
