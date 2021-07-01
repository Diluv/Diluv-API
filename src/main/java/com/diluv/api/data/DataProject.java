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

    public DataProject (ProjectsEntity project) {

        super(project);
        this.description = project.getDescription();
        this.links = project.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
        if (!project.getAuthors().isEmpty()) {
            this.contributors.addAll(project.getAuthors().stream().map(DataProjectContributor::new).collect(Collectors.toList()));
        }
    }

    public DataProject (ProjectsEntity project, List<DataProjectContributor> contributors) {

        super(project, contributors);
        this.description = project.getDescription();
        this.links = project.getLinks().stream().map(DataProjectLink::new).collect(Collectors.toList());
    }
}