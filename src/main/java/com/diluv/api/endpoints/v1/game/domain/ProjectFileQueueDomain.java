package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectFileQueueRecord;

public class ProjectFileQueueDomain extends BaseProjectFileDomain {

    private final String status;
    private final long statusChangeTime;

    public ProjectFileQueueDomain (ProjectFileQueueRecord record, String projectSlug, String projectTypeSlug, String gameSlug) {

        super(record, projectSlug, projectTypeSlug, gameSlug);
        this.status = record.getStatus();
        this.statusChangeTime = record.getStatusChangeTime();
    }

    public String getStatus () {

        return this.status;
    }

    public long getStatusChangeTime () {

        return this.statusChangeTime;
    }
}
