package com.diluv.api.endpoints.v1.game.project;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents the authorized view of the project data.
 */
public class DataProjectAuthorized extends DataProject {
    
    // TODO doc this
    @Expose
    private final List<String> permissions;
    
    // TODO doc this
    @Expose
    private final boolean released;
    
    // TODO doc this
    @Expose
    private final boolean review;
    
    public DataProjectAuthorized(ProjectRecord projectRecord, List<DataProjectContributor> projectAuthor, List<String> permissions) {
        
        super(projectRecord, projectAuthor);
        this.permissions = permissions;
        this.released = projectRecord.isReleased();
        this.review = projectRecord.isReview();
    }
}