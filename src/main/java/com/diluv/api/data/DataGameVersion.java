package com.diluv.api.data;

import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.google.gson.annotations.Expose;

public class DataGameVersion {

    //TODO
    @Expose
    private final String version;

    //TODO
    @Expose
    private final String type;

    //TODO
    @Expose
    private final long released;

    public DataGameVersion (GameVersionsEntity rs) {

        this.version = rs.getVersion();
        this.type = rs.getType();
        this.released = rs.getReleasedAt().getTime();
    }
}
