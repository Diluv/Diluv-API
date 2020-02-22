package com.diluv.api.endpoints.v1.game.project;

import java.util.ArrayList;
import java.util.List;

import com.diluv.confluencia.database.record.ProjectRecord;

public class DataProject {
    private final String name;
    private final String slug;
    private final String summary;
    private final String description;
    private final long cachedDownloads;
    private final long createdAt;
    private final long updatedAt;
    private final List<DataProjectAuthor> users = new ArrayList<>();
    
    public DataProject(ProjectRecord projectRecord) {
        
        this(projectRecord, null);
    }
    
    public DataProject(ProjectRecord projectRecord, List<DataProjectAuthor> projectAuthorRecords) {
        
        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.description = projectRecord.getDescription();
        this.cachedDownloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
        this.users.add(new DataProjectAuthor(projectRecord.getUsername(), "owner"));
        if (projectAuthorRecords != null) {
            this.users.addAll(projectAuthorRecords);
        }
    }
    
    public String getName () {
        
        return this.name;
    }
    
    public String getSlug () {
        
        return this.slug;
    }
    
    public String getSummary () {
        
        return this.summary;
    }
    
    public String getDescription () {
        
        return this.description;
    }
    
    public long getCachedDownloads () {
        
        return this.cachedDownloads;
    }
    
    public long getCreatedAt () {
        
        return this.createdAt;
    }
    
    public long getUpdatedAt () {
        
        return this.updatedAt;
    }
}
