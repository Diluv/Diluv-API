package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataCreateProject {

    @Expose
    private final List<DataSlugName> tags;

    public DataCreateProject (List<DataSlugName> tags) {

        this.tags = tags;
    }
}