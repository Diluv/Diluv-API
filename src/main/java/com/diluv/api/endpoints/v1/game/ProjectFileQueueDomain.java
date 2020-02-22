package com.diluv.api.endpoints.v1.game;

import com.diluv.confluencia.database.record.ProjectFileRecord;

public class ProjectFileQueueDomain extends BaseProjectFileDomain {
    
    private final String status;
    private final long statusChange;
    
    public ProjectFileQueueDomain(ProjectFileRecord record, String gameSlug, String projectTypeSlug, String projectSlug) {
        
        super(record, gameSlug, projectTypeSlug, projectSlug);
        this.status = record.getStatus().toString();
        this.statusChange = record.getStatusChange();
    }
    
    public String getStatus () {
        
        return this.status;
    }
    
    public long getStatusChange () {
        
        return this.statusChange;
    }
}
