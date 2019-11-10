package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectRecord {
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

    public ProjectRecord (ResultSet rs) throws SQLException {

        this.name = rs.getString("name");
        this.slug = rs.getString("slug");
        this.summary = rs.getString("summary");
        this.description = rs.getString("description");
        this.logoUrl = rs.getString("logo_url");
        this.cachedDownloads = rs.getLong("cached_downloads");
        this.createdAt = rs.getTimestamp("created_at").getTime();
        this.updatedAt = rs.getTimestamp("updated_at").getTime();
        this.game = rs.getString("game_name");
        this.username = rs.getString("owner_username");
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

    public String getLogoUrl () {

        return this.logoUrl;
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

    public String getGame () {

        return game;
    }

    public String getUsername () {

        return username;
    }
}
