package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project file that is still in our processing queue.
 */
public class DataProjectFileInQueue extends DataProjectFile {

    /**
     * The current processing status of the file.
     */
    @Expose
    private final String status;

    /**
     * When the project status last changed
     */
    @Expose
    private final long lastStatusChanged;

    public DataProjectFileInQueue (ProjectFileRecord record, List<Long> dependencies, List<DataGameVersion> gameVersions, String gameSlug, String projectTypeSlug, String projectSlug) {

        super(record, dependencies, gameVersions, gameSlug, projectTypeSlug, projectSlug);
        this.status = record.getProcessingStatus().toString();
        this.lastStatusChanged = record.getProcessingStatusChanged();
    }
}