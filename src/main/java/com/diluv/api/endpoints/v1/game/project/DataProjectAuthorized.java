package com.diluv.api.endpoints.v1.game.project;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectRecord;

public class DataProjectAuthorized extends DataProject {
    private final List<String> permissions;
    private final boolean released;
    private final boolean review;
    
    public DataProjectAuthorized(ProjectRecord projectRecord, List<DataProjectAuthor> projectAuthor, List<String> permissions) {
        
        super(projectRecord, projectAuthor);
        this.permissions = permissions;
        this.released = projectRecord.isReleased();
        this.review = projectRecord.isReview();
    }
    
    public List<String> getPermissions () {
        
        return this.permissions;
    }
}
