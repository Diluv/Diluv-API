package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataSiteGameProjectTypes {

    @Expose
    private final List<DataSlugName> types;

    public DataSiteGameProjectTypes (List<DataSlugName> types) {

        this.types = types;
    }
}
