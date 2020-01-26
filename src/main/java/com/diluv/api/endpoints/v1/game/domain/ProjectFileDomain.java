package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectFileRecord;

public class ProjectFileDomain {

    private final String name;
    private final String sha512;
    private final long size;
    private final String changelog;
    private final long createdAt;
    private final long updatedAt;

    public ProjectFileDomain (ProjectFileRecord rs) {

        this.name = rs.getName();
        this.sha512 = rs.getSha512();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt();
        this.updatedAt = rs.getUpdatedAt();
    }

    public String getName () {

        return this.name;
    }

    public String getSha512 () {

        return this.sha512;
    }

    public long getSize () {

        return this.size;
    }

    public String getChangelog () {

        return this.changelog;
    }

    public long getCreatedAt () {

        return this.createdAt;
    }

    public long getUpdatedAt () {

        return this.updatedAt;
    }
}
