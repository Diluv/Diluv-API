package com.diluv.api.data;

import com.diluv.confluencia.database.record.NewsRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a news post on the site.
 */
public class DataNewsPost {
    
    /**
     * The slug for the news post.
     */
    @Expose
    private final String slug;
    
    /**
     * The title of the post.
     */
    @Expose
    private final String title;
    
    /**
     * A short summary of the post.
     */
    @Expose
    private final String summary;
    
    /**
     * The description body of the post.
     */
    @Expose
    private final String description;
    
    /**
     * The username of the account that created the post.
     */
    @Expose
    private final String username;
    
    /**
     * The date when the post was created.
     */
    @Expose
    private final long createdAt;
    
    public DataNewsPost(NewsRecord news) {
        
        this.slug = news.getSlug();
        this.title = news.getTitle();
        this.summary = news.getSummary();
        this.description = news.getDescription();
        this.username = news.getUsername();
        this.createdAt = news.getCreatedAt();
    }
}