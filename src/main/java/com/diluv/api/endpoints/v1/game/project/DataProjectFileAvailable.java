package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project file that is publicly available.
 */
public class DataProjectFileAvailable extends DataProjectFile {
    
    /**
     * The SHA-512 hash of the file.
     */
    @Expose
    private final String sha512;
    
    /**
     * The last time the file was updated.
     */
    @Expose
    private final long updatedAt;
    
    public DataProjectFileAvailable(ProjectFileRecord rs, String projectSlug, String projectTypeSlug, String gameSlug) {
        
        super(rs, projectSlug, projectTypeSlug, gameSlug);
        this.sha512 = rs.getSha512();
        this.updatedAt = rs.getUpdatedAt();
    }
}