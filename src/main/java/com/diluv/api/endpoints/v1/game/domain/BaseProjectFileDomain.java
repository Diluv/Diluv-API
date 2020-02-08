package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.BaseProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;

public class BaseProjectFileDomain {

    private final long id;
    private final String name;
    private final long size;
    private final String changelog;
    private final long createdAt;
    private final String projectSlug;
    private final String projectTypeSlug;
    private final String gameSlug;
    private final String username;

    public BaseProjectFileDomain (BaseProjectFileRecord rs, String projectSlug, String projectTypeSlug, String gameSlug) {

        this.id = rs.getId();
        this.name = rs.getName();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt();
        this.username = rs.getUsername();
        this.projectSlug = projectSlug;
        this.projectTypeSlug = projectTypeSlug;
        this.gameSlug = gameSlug;
    }

    public long getId () {

        return this.id;
    }

    public String getName () {

        return this.name;
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

    public String getProjectSlug () {

        return this.projectSlug;
    }

    public String getProjectTypeSlug () {

        return this.projectTypeSlug;
    }

    public String getGameSlug () {

        return this.gameSlug;
    }

    public String getUsername () {

        return this.username;
    }
}
