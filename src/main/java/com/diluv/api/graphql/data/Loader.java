package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;

public class Loader {

    private long id;
    private String slug;
    private String name;

    private ProjectTypeLoadersEntity entity;

    public Loader (ProjectTypeLoadersEntity entity) {

        this.id = entity.getId();
        this.slug = entity.getSlug();
        this.name = entity.getName();

        this.entity = entity;
    }

    public ProjectTypeLoadersEntity getEntity () {

        return this.entity;
    }
}
