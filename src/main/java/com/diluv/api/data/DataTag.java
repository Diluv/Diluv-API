package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectTagsEntity;
import com.diluv.confluencia.database.record.TagsEntity;
import com.google.gson.annotations.Expose;

public class DataTag {

    @Expose
    private final String slug;

    @Expose
    private final String name;

    public DataTag (TagsEntity rs) {

        this.slug = rs.getSlug();
        this.name = rs.getName();
    }

    public DataTag (ProjectTagsEntity rs) {

        this(rs.getTag());
    }
}
