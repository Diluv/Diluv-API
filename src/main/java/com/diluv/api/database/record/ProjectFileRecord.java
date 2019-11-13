package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ProjectFileRecord {

    private long id;
    private String sha512;
    private String crc32;
    private long size;
    private String changelog;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean reviewed;
    private boolean released;
    private long projectId;
    private long userId;


    public ProjectFileRecord () {
        
    }

    public ProjectFileRecord (ResultSet rs) throws SQLException {

        this.id = rs.getLong("id");
        this.sha512 = rs.getString("sha512");
        this.crc32 = rs.getString("crc32");
        this.size = rs.getLong("size");
        this.changelog = rs.getString("changelog");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.reviewed = rs.getBoolean("reviewed");
        this.released = rs.getBoolean("released");
        this.projectId = rs.getLong("project_id");
        this.userId = rs.getLong("user_id");
    }

    public long getId () {

        return this.id;
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

    public Timestamp getCreatedAt () {

        return this.createdAt;
    }

    public Timestamp getUpdatedAt () {

        return this.updatedAt;
    }

    public boolean isReviewed () {

        return this.reviewed;
    }

    public boolean isReleased () {

        return this.released;
    }

    public long getProjectId () {

        return projectId;
    }

    public long getUserId () {

        return userId;
    }
}
