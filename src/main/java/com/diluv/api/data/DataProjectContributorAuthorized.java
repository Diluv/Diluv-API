package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project contributor from the perspective of an authorized user.
 */
public class DataProjectContributorAuthorized extends DataProjectContributor {

    /**
     * The permissions the auth has.
     */
    @Expose
    private final List<String> permissions;
    
    public DataProjectContributorAuthorized (ProjectAuthorRecord projectAuthor) {
        
        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions();
    }
}