package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseGame;
import com.diluv.api.data.DataGameVersion;
import com.diluv.confluencia.database.record.GamesEntity;
import com.google.gson.annotations.Expose;

public class DataSiteGameVersion extends DataBaseGame {

    @Expose
    private final List<DataGameVersion> versions;

    public DataSiteGameVersion (GamesEntity rs, List<DataGameVersion> versions) {

        super(rs);
        this.versions = versions;
    }

    public DataSiteGameVersion (String slug, String name, List<DataGameVersion> versions) {

        super(slug, name);
        this.versions = versions;
    }
}
