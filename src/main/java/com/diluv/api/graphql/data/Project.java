package com.diluv.api.graphql.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectsEntity;

public class Project {

    private final long id;
    private final String name;
    private final String slug;
    private final String summary;
    private final String description;
    private final boolean review;
    private final boolean released;
    private final long downloads;
    private final long createdAt;
    private final long updatedAt;
    private final String logo;

    private ProjectsEntity entity;

    public Project (ProjectsEntity entity) {

        this.id = entity.getId();
        this.name = entity.getName();
        this.slug = entity.getSlug();
        this.summary = entity.getSummary();
        this.description = entity.getDescription();
        this.downloads = entity.getCachedDownloads();
        this.review = entity.isReview();
        this.released = entity.isReleased();
        this.createdAt = entity.getCreatedAt().getTime();
        this.updatedAt = entity.getCreatedAt().getTime();
        this.logo = Constants.getProjectLogo(entity);

        this.entity = entity;
    }

    public ProjectsEntity getEntity () {

        return this.entity;
    }
}
