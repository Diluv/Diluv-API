package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ProjectRecord {
    private long id;
    private String name;
    private String slug;
    private String summary;
    private String description;
    private long cachedDownloads;
    private long createdAt;
    private long updatedAt;
    private long userId;
    private String gameSlug;
    private String projectTypeSlug;

    public ProjectRecord () {

    }

    public ProjectRecord (ResultSet rs) throws SQLException {

        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.slug = rs.getString("slug");
        this.summary = rs.getString("summary");
        this.description = rs.getString("description");
        this.cachedDownloads = rs.getLong("cached_downloads");
        this.createdAt = rs.getTimestamp("created_at").getTime();
        this.updatedAt = rs.getTimestamp("updated_at").getTime();
        this.userId = rs.getLong("user_id");
        this.gameSlug = rs.getString("game_slug");
        this.projectTypeSlug = rs.getString("project_type_slug");
    }

    @JsonCreator
    public ProjectRecord (int id, String name, String slug, String summary, String description, long cachedDownloads, long createdAt, long updatedAt, long userId, String gameSlug, String projectTypeSlug) {

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.summary = summary;
        this.description = description;
        this.cachedDownloads = cachedDownloads;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.gameSlug = gameSlug;
        this.projectTypeSlug = projectTypeSlug;
    }

    public long getId () {

        return this.id;
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

    public long getUserId () {

        return this.userId;
    }

    public String getGameSlug () {

        return gameSlug;
    }

    public String getProjectTypeSlug () {

        return projectTypeSlug;
    }
}
