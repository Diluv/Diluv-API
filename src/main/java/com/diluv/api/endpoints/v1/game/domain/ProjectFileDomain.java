package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.api.database.record.ProjectFileRecord;

public class ProjectFileDomain {

    private final String sha512;
    private final String crc32;
    private final long size;
    private final String changelog;
    private final long createdAt;
    private final long updatedAt;

    public ProjectFileDomain (ProjectFileRecord rs) {

        // TODO Add missing file name
        this.sha512 = rs.getSha512();
        this.crc32 = rs.getCrc32();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt().getTime();
        this.updatedAt = rs.getUpdatedAt().getTime();
    }

    public String getSha512 () {

        return sha512;
    }

    public String getCrc32 () {

        return crc32;
    }

    public long getSize () {

        return size;
    }

    public String getChangelog () {

        return changelog;
    }

    public long getCreatedAt () {

        return createdAt;
    }

    public long getUpdatedAt () {

        return updatedAt;
    }
}
