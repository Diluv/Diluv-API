package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RefreshTokenRecord {

    private long userId;
    private String code;
    private long expiredAt;

    public RefreshTokenRecord () {

    }

    public RefreshTokenRecord (ResultSet rs) throws SQLException {

        this.userId = rs.getLong("user_id");
        this.code = rs.getString("code");
        this.expiredAt = rs.getTimestamp("expired_at").getTime();
    }

    public long getUserId () {

        return this.userId;
    }

    public String getCode () {

        return this.code;
    }

    public long getExpiredAt () {

        return this.expiredAt;
    }
}
