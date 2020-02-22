package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;

public class DataProjectAuthor {
    private final String username;
    private final String role;
    
    public DataProjectAuthor(ProjectAuthorRecord projectAuthor) {
        
        this.username = projectAuthor.getUsername();
        this.role = projectAuthor.getRole();
    }
    
    public DataProjectAuthor(String username, String role) {
        
        this.username = username;
        this.role = role;
    }
}
