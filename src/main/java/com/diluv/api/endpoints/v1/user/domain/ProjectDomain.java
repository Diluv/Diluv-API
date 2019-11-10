package com.diluv.api.endpoints.v1.user.domain;

import com.diluv.api.database.record.ProjectRecord;

public class ProjectDomain {
    private final String name;
    private final String slug;
    private final String summary;
    private final String description;
    private final String logoUrl;
    private final long cachedDownloads;
    private final long createdAt;
    private final long updatedAt;
    private final String game;
    private final String username;

    public ProjectDomain (ProjectRecord projectRecord) {

        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.description = projectRecord.getDescription();
        this.logoUrl = projectRecord.getLogoUrl();
        this.cachedDownloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
        this.game = projectRecord.getGame();
        this.username = projectRecord.getUsername();
    }

    public String getName () {

        return name;
    }

    public String getSlug () {

        return slug;
    }

    public String getSummary () {

        return summary;
    }

    public String getDescription () {

        return description;
    }

    public String getLogoUrl () {

        return logoUrl;
    }

    public long getCachedDownloads () {

        return cachedDownloads;
    }

    public long getCreatedAt () {

        return createdAt;
    }

    public long getUpdatedAt () {

        return updatedAt;
    }

    public String getGame () {

        return game;
    }

    public String getUsername () {

        return username;
    }
}
