package com.diluv.api.data;

import com.diluv.confluencia.database.record.GameVersionRecord;
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

    public DataGameVersion (GameVersionRecord rs) {

        this.version = rs.getVersion();
        this.type = rs.getType();
        this.released = rs.getReleased();
    }
}
