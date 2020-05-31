package com.diluv.api.data;

import java.util.List;

public class DataProjectList {

    private final DataBaseGame game;
    private final DataBaseProjectType projectType;
    private final List<DataTag> tags;
    private final List<DataGameVersion> gameVersions;
    private final List<DataProjectType> projectTypes;
    private final List<DataProject> projects;
    private final long projectCount;
    private final List<DataSort> sort;

    public DataProjectList (DataBaseGame game,
                            DataBaseProjectType projectType,
                            List<DataTag> tags,
                            List<DataGameVersion> gameVersions,
                            List<DataProjectType> projectTypes,
                            List<DataProject> projects,
                            long projectCount,
                            List<DataSort> sort) {

        this.game = game;
        this.projectType = projectType;
        this.tags = tags;
        this.gameVersions = gameVersions;
        this.projectTypes = projectTypes;
        this.projects = projects;
        this.projectCount = projectCount;
        this.sort = sort;
    }
}
