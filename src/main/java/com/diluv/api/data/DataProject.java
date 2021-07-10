package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.database.record.ProjectsEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a project on the site.
 */
public class DataProject extends DataBaseProject {

    /**
     * The description of the project.
     */
    @Expose
    private final String description;

    @Expose
    private final List<DataProjectLink> links;

    public DataProject (ProjectsEntity rs) {

        super(rs);
        this.description = rs.getDescription();
        this.links = rs.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
    }

    public DataProject (ProjectsEntity rs, DataUser owner, List<DataProjectAuthor> authors) {

        super(rs, owner, authors);
        this.description = rs.getDescription();
        this.links = rs.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
    }
}