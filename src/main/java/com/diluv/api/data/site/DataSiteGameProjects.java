package com.diluv.api.data.site;

import com.diluv.api.data.DataProjectType;
import com.google.gson.annotations.Expose;

public class DataSiteGameProjects {

    @Expose
    private final DataProjectType currentType;

    public DataSiteGameProjects (DataProjectType currentType) {

        this.currentType = currentType;
    }
}
