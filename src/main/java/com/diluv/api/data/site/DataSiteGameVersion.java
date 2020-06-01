package com.diluv.api.data.site;

import com.diluv.api.data.DataBaseGame;
import com.diluv.api.data.DataGameVersion;
import com.diluv.confluencia.database.record.GameRecord;
import com.google.gson.annotations.Expose;

import java.util.List;

public class DataSiteGameVersion extends DataBaseGame {

    @Expose
    private final List<DataGameVersion> versions;

    public DataSiteGameVersion (GameRecord rs, List<DataGameVersion> versions) {

        super(rs);
        this.versions = versions;
    }

    public DataSiteGameVersion (String slug, String name, List<DataGameVersion> versions) {

        super(slug, name);
        this.versions = versions;
    }
}
