package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectFileRecord;

public class ProjectFileQueueDomain extends BaseProjectFileDomain {

    private final String status;
    private final long statusChangeTime;

    public ProjectFileQueueDomain (ProjectFileRecord record, String gameSlug, String projectTypeSlug, String projectSlug) {

        super(record, gameSlug, projectTypeSlug, projectSlug);
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
