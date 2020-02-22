package com.diluv.api.endpoints.v1.game.domain;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectRecord;

public class AuthorizedProjectDomain extends ProjectDomain {
    private final List<String> permissions;
    private final boolean released;
    private final boolean review;
    
    public AuthorizedProjectDomain(ProjectRecord projectRecord, List<ProjectAuthorDomain> projectAuthor, List<String> permissions) {
        
        super(projectRecord, projectAuthor);
        this.permissions = permissions;
        this.released = projectRecord.isReleased();
        this.review = projectRecord.isReview();
    }
    
    public List<String> getPermissions () {
        
        return this.permissions;
    }
}
