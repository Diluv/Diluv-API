package com.diluv.api.endpoints.v1.news;

import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.confluencia.database.record.NewsRecord;

/**
 * Represents a news post on the site.
 */
public class DataNewsPost implements IResponse {
    
    /**
     * The slug for the news post.
     */
    private final String slug;
    
    /**
     * The title of the post.
     */
    private final String title;
    
    /**
     * A short summary of the post.
     */
    private final String summary;
    
    /**
     * The description body of the post.
     */
    private final String description;
    
    /**
     * The username of the account that created the post.
     */
    private final String username;
    
    /**
     * The date when the post was created.
     */
    private final long createdAt;
    
    public DataNewsPost(NewsRecord news) {
        
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
