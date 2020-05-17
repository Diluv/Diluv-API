package com.diluv.api.data;

import com.diluv.confluencia.database.record.TagRecord;
import com.google.gson.annotations.Expose;

public class DataTag {

    @Expose
    private final String slug;

    @Expose
    private final String name;

    public DataTag (TagRecord rs) {

        this.slug = rs.getSlug();
        this.name = rs.getName();
    }
}
