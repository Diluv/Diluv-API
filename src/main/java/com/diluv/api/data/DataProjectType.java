package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a supported project type for a supported game.
 */
public class DataProjectType extends DataBaseProjectType {

    /**
     * The slug of the game the project type belongs to.
     */
    @Expose
    private final DataBaseGame game;

    @Expose
    private final List<DataTag> tags;

    @Expose
    private final long projectCount;

    public DataProjectType (ProjectTypeRecord rs) {

        this(rs, null);
    }

    public DataProjectType (ProjectTypeRecord rs, List<DataTag> tags) {

        super(rs);
        this.game = new DataBaseGame(rs.getGameSlug(), rs.getGameName());
        this.projectCount = rs.getProjectCount();

        this.tags = tags;
    }
}