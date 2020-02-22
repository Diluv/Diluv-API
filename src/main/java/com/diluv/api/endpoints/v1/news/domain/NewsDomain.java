package com.diluv.api.endpoints.v1.news.domain;

import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.confluencia.database.record.NewsRecord;

public class NewsDomain implements Domain {
    
    private final String slug;
    private final String title;
    private final String summary;
    private final String description;
    private final String username;
    private final long createdAt;
    
    public NewsDomain(NewsRecord news) {
        
        this.slug = news.getSlug();
        this.title = news.getTitle();
        this.summary = news.getSummary();
        this.description = news.getDescription();
        this.username = news.getUsername();
        this.createdAt = news.getCreatedAt();
    }
    
    public String getSlug () {
        
        return this.slug;
    }
    
    public String getTitle () {
        
        return this.title;
    }
    
    public String getSummary () {
        
        return this.summary;
    }
    
    public String getDescription () {
        
        return this.description;
    }
    
    public String getUsername () {
        
        return this.username;
    }
    
    public long getCreatedAt () {
        
        return this.createdAt;
    }
}
