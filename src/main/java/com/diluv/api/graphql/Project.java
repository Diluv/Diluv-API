package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.ProjectsEntity;

public class Project {

    private long id;
    private String name;
    private ProjectsEntity entity;

    public Project (ProjectsEntity entity) {

        this.id = entity.getId();
        this.name = entity.getName();
        this.entity = entity;
    }

    public long getId () {

        return this.id;
    }

    public String getName () {

        return this.name;
    }

    public ProjectsEntity getEntity () {

        return this.entity;
    }
}
