package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectFileRecord;

/**
 * Represents a file uploaded to a project.
 */
public class DataProjectFile {
    
    /**
     * An internal id for the file.
     */
    private final long id;
    
    /**
     * The display name of the file.
     */
    private final String name;
    
    /**
     * The byte size of the file.
     */
    private final long size;
    
    /**
     * The changelog or description for the file.
     */
    private final String changelog;
    
    /**
     * The time when the file was created.
     */
    private final long createdAt;
    
    /**
     * The slug of the game the project belongs to.
     */
    private final String gameSlug;
    
    /**
     * The slug of the project's type.
     */
    private final String projectTypeSlug;
    
    /**
     * The slug of the project.
     */
    private final String projectSlug;
    
    // TODO doc this
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
    
    public long getId () {
        
        return this.id;
    }
    
    public String getName () {
        
        return this.name;
    }
    
    public long getSize () {
        
        return this.size;
    }
    
    public String getChangelog () {
        
        return this.changelog;
    }
    
    public long getCreatedAt () {
        
        return this.createdAt;
    }
    
    public String getProjectSlug () {
        
        return this.projectSlug;
    }
    
    public String getProjectTypeSlug () {
        
        return this.projectTypeSlug;
    }
    
    public String getGameSlug () {
        
        return this.gameSlug;
    }
    
    public String getUsername () {
        
        return this.username;
    }
}
