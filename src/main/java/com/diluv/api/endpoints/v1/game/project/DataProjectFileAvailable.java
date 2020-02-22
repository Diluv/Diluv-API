package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectFileRecord;

public class DataProjectFileAvailable extends DataProjectFile {
    
    private final String sha512;
    private final long updatedAt;
    
    public DataProjectFileAvailable(ProjectFileRecord rs, String projectSlug, String projectTypeSlug, String gameSlug) {
        
        super(rs, projectSlug, projectTypeSlug, gameSlug);
        this.sha512 = rs.getSha512();
        this.updatedAt = rs.getUpdatedAt();
    }
    
    public String getSha512 () {
        
        return this.sha512;
    }
    
    public long getUpdatedAt () {
        
        return this.updatedAt;
    }
}
