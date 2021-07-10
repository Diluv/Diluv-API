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

    /**
     * The users who contributed to the project.
     */
    @Expose
    public final List<DataProjectContributor> contributors;

    public DataProject (ProjectsEntity project) {

        super(project);
        this.description = project.getDescription();
        this.links = project.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
        this.contributors = project.getAuthors().stream().map(DataProjectContributor::new).collect(Collectors.toList());
    }

    public DataProject (ProjectsEntity project, DataUser owner, List<DataProjectContributor> contributors) {

        super(project, owner);
        this.description = project.getDescription();
        this.links = project.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
        this.contributors = contributors;
    }
}