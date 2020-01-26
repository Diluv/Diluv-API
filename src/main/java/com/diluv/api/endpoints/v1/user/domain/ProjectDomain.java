package com.diluv.api.endpoints.v1.user.domain;

import com.diluv.confluencia.database.record.ProjectRecord;

public class ProjectDomain {
    private final String name;
    private final String slug;
    private final String summary;
    private final String description;
    private final long cachedDownloads;
    private final long createdAt;
    private final long updatedAt;

    public ProjectDomain (ProjectRecord projectRecord) {

        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.description = projectRecord.getDescription();
        this.cachedDownloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
    }

    public String getName () {

        return this.name;
    }

    public String getSlug () {

        return this.slug;
    }

    public String getSummary () {

        return this.summary;
    }

    public String getDescription () {

        return this.description;
    }

    public long getCachedDownloads () {

        return this.cachedDownloads;
    }

    public long getCreatedAt () {

        return this.createdAt;
    }

    public long getUpdatedAt () {

        return this.updatedAt;
    }
}
