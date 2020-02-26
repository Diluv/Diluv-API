package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a file uploaded to a project.
 */
public class DataProjectFile {
    
    /**
     * An internal id for the file.
     */
    @Expose
    private final long id;
    
    /**
     * The display name of the file.
     */
    @Expose
    private final String name;
    
    /**
     * The byte size of the file.
     */
    @Expose
    private final long size;
    
    /**
     * The changelog or description for the file.
     */
    @Expose
    private final String changelog;
    
    /**
     * The time when the file was created.
     */
    @Expose
    private final long createdAt;
    
    /**
     * The slug of the game the project belongs to.
     */
    @Expose
    private final String gameSlug;
    
    /**
     * The slug of the project's type.
     */
    @Expose
    private final String projectTypeSlug;
    
    /**
     * The slug of the project.
     */
    @Expose
    private final String projectSlug;
    
    // TODO doc this
    @Expose
    private final String username;
    
    public DataProjectFile(ProjectFileRecord rs, String gameSlug, String projectTypeSlug, String projectSlug) {
        
        this.id = rs.getId();
        this.name = rs.getName();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt();
        this.username = rs.getUsername();
        this.gameSlug = gameSlug;
        this.projectTypeSlug = projectTypeSlug;
        this.projectSlug = projectSlug;
    }
}