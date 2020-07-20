package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectsEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents the authorized view of the project data.
 */
public class DataProjectAuthorized extends DataProject {

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

    public DataProjectAuthorized (ProjectsEntity projectRecord, List<String> permissions) {

        super(projectRecord);
        this.released = projectRecord.isReleased();
        this.review = projectRecord.isReview();
        this.permissions = permissions;
    }
}