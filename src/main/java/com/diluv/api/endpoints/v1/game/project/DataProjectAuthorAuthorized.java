package com.diluv.api.endpoints.v1.game.project;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectAuthorRecord;

public class DataProjectAuthorAuthorized extends DataProjectAuthor {
    private final List<String> permissions;
    
    public DataProjectAuthorAuthorized(ProjectAuthorRecord projectAuthor) {
        
        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions();
    }
    
    public List<String> getPermissions () {
        
        return this.permissions;
    }
}
