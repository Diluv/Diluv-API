package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.api.database.record.ProjectFileRecord;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ProjectFileDomain {

    private final String name;
    private final String sha512;
    private final String crc32;
    private final long size;
    private final String changelog;
    private final long createdAt;
    private final long updatedAt;

    @JsonCreator
    public ProjectFileDomain (ProjectFileRecord rs) {

        this.name = rs.getName();
        this.sha512 = rs.getSha512();
        this.crc32 = rs.getCrc32();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt().getTime();
        this.updatedAt = rs.getUpdatedAt().getTime();
    }

    public String getName () {

        return this.name;
    }

    public String getSha512 () {

        return this.sha512;
    }

    public String getCrc32 () {

        return this.crc32;
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
