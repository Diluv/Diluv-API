package com.diluv.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents the authorized view of the project data.
 */
public class DataAuthorizedProject extends DataProject {

    /**
     * Is the project released to the public.
     */
    @Expose
    private final boolean released;

    /**
     * If the project needs a review from the dev team.
     */
    @Expose
    private final boolean review;

    /**
     * The permissions the authorize user has.
     */
    @Expose
    private final List<String> permissions;

    public DataAuthorizedProject (ProjectsEntity project, List<String> permissions) {

        super(project, getAuthorizedContributors(project));
        this.released = project.isReleased();
        this.review = project.isReview();
        this.permissions = permissions;
    }

    public static List<DataProjectContributor> getAuthorizedContributors (ProjectsEntity rs) {

        List<DataProjectContributor> contributors = new ArrayList<>();
        contributors.add(new DataAuthorizedProjectContributor(rs.getOwner(), "owner", ProjectPermissions.getAllPermissions()));
        if (!rs.getAuthors().isEmpty()) {
            contributors.addAll(rs.getAuthors().stream().map(DataAuthorizedProjectContributor::new).collect(Collectors.toList()));
        }
        return contributors;
    }
}