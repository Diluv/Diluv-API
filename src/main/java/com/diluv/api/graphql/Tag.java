package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.TagsEntity;

public class Tag {
    private long id;
    private String slug;
    private String name;

    private TagsEntity entity;

    public Tag (TagsEntity entity) {

        this.id = entity.getId();
        this.slug = entity.getSlug();
        this.name = entity.getName();

        this.entity = entity;
    }

    public TagsEntity getEntity () {

        return this.entity;
    }
}
