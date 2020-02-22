package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectFileRecord;

public class DataProjectFileInQueue extends DataProjectFile {
    
    private final String status;
    private final long statusChange;
    
    public DataProjectFileInQueue(ProjectFileRecord record, String gameSlug, String projectTypeSlug, String projectSlug) {
        
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
