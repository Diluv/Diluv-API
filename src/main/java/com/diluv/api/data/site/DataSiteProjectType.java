package com.diluv.api.data.site;

import com.diluv.api.data.DataProjectType;
import com.diluv.api.data.DataTag;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.google.gson.annotations.Expose;

import java.util.List;

public class DataSiteProjectType extends DataProjectType {

    @Expose
    private final DataSiteGameVersion game;

    public DataSiteProjectType (ProjectTypeRecord rs, DataSiteGameVersion game) {

        super(rs);
        this.game = game;
    }

    public DataSiteProjectType (ProjectTypeRecord rs, List<DataTag> tags, DataSiteGameVersion game) {

        super(rs, tags);
        this.game = game;
    }
}
