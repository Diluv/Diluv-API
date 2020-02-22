package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;

/**
 * Represents a user who contributed to a project.
 */
public class DataProjectContributor {
    
    /**
     * The username of the contributor.
     */
    private final String username;
    
    /**
     * The role the contributor played in the creation of the project.
     */
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
