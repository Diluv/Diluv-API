package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameRecord {
    private final String slug;
    private final String name;
    private final String url;

    public GameRecord (ResultSet rs) throws SQLException {

        this.slug = rs.getString("slug");
        this.name = rs.getString("name");
        this.url = rs.getString("url");
    }

    public String getSlug () {

        return slug;
    }

    public String getName () {

        return this.name;
    }

    public String getUrl () {

        return this.url;
    }
}
