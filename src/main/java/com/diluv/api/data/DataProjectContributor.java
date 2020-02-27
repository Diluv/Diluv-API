package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a user who contributed to a project.
 */
public class DataProjectContributor {
    
    /**
     * The user name of the contributor.
     */
    @Expose
    private final String username;
    
    /**
     * The role the contributor played in the creation of the project.
     */
    @Expose
    private final String role;
    
    public DataProjectContributor(ProjectAuthorRecord projectAuthor) {
        
        this.username = projectAuthor.getUsername();
        this.role = projectAuthor.getRole();
    }
    
    public DataProjectContributor(String username, String role) {
        
        this.username = username;
        this.role = role;
    }
}