package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataTag;
import com.google.gson.annotations.Expose;

public class DataCreateProject {

    @Expose
    private final List<DataTag> tags;

    public DataCreateProject (List<DataTag> tags) {

        this.tags = tags;
    }
}