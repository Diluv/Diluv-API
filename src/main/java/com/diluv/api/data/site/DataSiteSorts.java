package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataSiteSorts {

    @Expose
    private final List<DataSlugName> game;

    @Expose
    private final List<DataSlugName> project;

    @Expose
    private final List<DataSlugName> projectFile;

    public DataSiteSorts (List<DataSlugName> game, List<DataSlugName> project, List<DataSlugName> projectFile) {

        this.game = game;
        this.project = project;
        this.projectFile = projectFile;
    }
}
