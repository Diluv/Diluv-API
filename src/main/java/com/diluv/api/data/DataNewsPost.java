package com.diluv.api.data;

import com.diluv.confluencia.database.record.NewsEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a news post on the site.
 */
public class DataNewsPost extends DataSlugName {

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

    //TODO
    @Expose
    private final String displayName;

    /**
     * The date when the post was created.
     */
    @Expose
    private final String createdAt;

    public DataNewsPost (NewsEntity news) {

        super(news.getSlug(), news.getTitle());
        this.summary = news.getSummary();
        this.description = news.getDescription();
        this.username = news.getUser().getUsername();
        this.displayName = news.getUser().getDisplayName();
        this.createdAt = news.getCreatedAt().toString();
    }
}