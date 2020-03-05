package com.diluv.api.data;

import com.diluv.confluencia.database.record.GameVersionRecord;
import com.google.gson.annotations.Expose;

public class DataGameVersion {

    @Expose
    private final String version;

    @Expose
    private final String changelogURL;

    public DataGameVersion (GameVersionRecord rs) {

        this.version = rs.getVersion();
        this.changelogURL = rs.getChangelogURL();
    }
}
