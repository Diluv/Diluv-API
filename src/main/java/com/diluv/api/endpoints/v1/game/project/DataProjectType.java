package com.diluv.api.endpoints.v1.game.project;

import com.diluv.confluencia.database.record.ProjectTypeRecord;

/**
 * Represents a supported project type for a supported game.
 */
public class DataProjectType {
    
    /**
     * The display name for the project type.
     */
    private final String name;
    
    /**
     * The slug for the project type.
     */
    private final String slug;
    
    /**
     * The slug of the game the project type belongs to.
     */
    private final String gameSlug;
    
    /**
     * The default max byte size for files of this type.
     */
    private final long maxSize;
    
    public DataProjectType(ProjectTypeRecord rs) {
        
        this.name = rs.getName();
        this.slug = rs.getSlug();
        this.gameSlug = rs.getGameSlug();
        this.maxSize = rs.getMaxSize();
    }
    
    public String getName () {
        
        return this.name;
    }
    
    public String getSlug () {
        
        return this.slug;
    }
    
    public String getGameSlug () {
        
        return this.gameSlug;
    }
    
    public long getMaxSize () {
        
        return this.maxSize;
    }
}
