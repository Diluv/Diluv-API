package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectFileRecord;

/**
 * Represents a project file that is still in our processing queue.
 */
public class DataProjectFileInQueue extends DataProjectFile {
    
    /**
     * The current processing status of the file.
     */
    private final String status;
    
    // TODO doc this
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
