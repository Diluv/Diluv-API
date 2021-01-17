package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.ProjectTypesEntity;

public class ProjectType {

    private String slug;
    private String name;
    private long maxFileSize;

    private ProjectTypesEntity entity;

    public ProjectType (ProjectTypesEntity entity) {

        this.slug = entity.getSlug();
        this.name = entity.getName();
        this.maxFileSize = entity.getMaxFileSize();

        this.entity = entity;
    }

    public ProjectTypesEntity getEntity () {

        return this.entity;
    }
}
