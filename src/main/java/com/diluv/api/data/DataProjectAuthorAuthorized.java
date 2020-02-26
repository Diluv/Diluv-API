package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project contributor from the perspective of an authorized user.
 */
public class DataProjectAuthorAuthorized extends DataProjectContributor {
    
    // TODO doc this
    @Expose
    private final List<String> permissions;
    
    public DataProjectAuthorAuthorized(ProjectAuthorRecord projectAuthor) {
        
        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions();
    }
}