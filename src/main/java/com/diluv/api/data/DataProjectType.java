package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.database.record.ProjectTypesEntity;
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
    private final Long projectCount;

    @Expose
    private final List<DataLoader> loaders;

    public DataProjectType (ProjectTypesEntity rs) {

        super(rs);
        this.game = new DataBaseGame(rs.getGame());
        this.tags = rs.getTags().stream().map(DataTag::new).collect(Collectors.toList());
        this.projectCount = null;
        this.loaders = rs.getProjectTypeLoaders().stream().map(DataLoader::new).collect(Collectors.toList());
    }

    public DataProjectType (ProjectTypesEntity rs, long projectCount) {

        super(rs);
        this.game = new DataBaseGame(rs.getGame());
        this.tags = rs.getTags().stream().map(DataTag::new).collect(Collectors.toList());
        this.projectCount = projectCount;
        this.loaders = rs.getProjectTypeLoaders().stream().map(DataLoader::new).collect(Collectors.toList());
    }
}