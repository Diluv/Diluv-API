package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.ProjectsEntity;

public class Project {

    private long id;
    private String name;
    private String slug;
    private String summary;
    private String description;
    private long cachedDownloads;
    private boolean review;
    private boolean released;
    private long createdAt;
    private long updatedAt;

    private ProjectsEntity entity;

    public Project (ProjectsEntity entity) {

        this.id = entity.getId();
        this.name = entity.getName();
        this.slug = entity.getSlug();
        this.summary = entity.getSummary();
        this.description = entity.getDescription();
        this.cachedDownloads = entity.getCachedDownloads();
        this.review = entity.isReview();
        this.released = entity.isReleased();
        this.createdAt = entity.getCreatedAt().getTime();
        this.updatedAt = entity.getCreatedAt().getTime();

        this.entity = entity;
    }

    public ProjectsEntity getEntity () {

        return this.entity;
    }
}
