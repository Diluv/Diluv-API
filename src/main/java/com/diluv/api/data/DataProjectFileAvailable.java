package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a project file that is publicly available.
 */
public class DataProjectFileAvailable extends DataProjectFile {

    /**
     * The last time the file was updated.
     */
    @Expose
    private final long updatedAt;

    public DataProjectFileAvailable (ProjectFilesEntity rs, String projectSlug, String projectTypeSlug,
                                     String gameSlug) {

        super(rs, projectSlug, projectTypeSlug, gameSlug);
        this.updatedAt = rs.getUpdatedAt().getTime();
    }
}