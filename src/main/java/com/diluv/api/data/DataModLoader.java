package com.diluv.api.data;

import com.diluv.confluencia.database.record.ModLoaderRecord;
import com.google.gson.annotations.Expose;

public class DataModLoader {

    @Expose
    private final String name;

    public DataModLoader (ModLoaderRecord rs) {

        this.name = rs.getName();
    }
}
