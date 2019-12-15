package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserRecord extends BaseUserRecord {
    private boolean mfa;
    private String mfaSecret;
    private String avatarUrl;

    public UserRecord () {

    }

    public UserRecord (ResultSet rs) throws SQLException {

        super(rs);
        this.mfa = rs.getBoolean("mfa");
        this.mfaSecret = rs.getString("mfa_secret");
        this.avatarUrl = rs.getString("avatar_url");
    }

    public UserRecord (long id, String email, String username, String password, String passwordType, boolean mfa, String mfaSecret, String avatarUrl, Timestamp createdAt) {

        super(id, email, username, password, passwordType, createdAt);
        this.mfa = mfa;
        this.mfaSecret = mfaSecret;
        this.avatarUrl = avatarUrl;
    }

    public boolean isMfa () {

        return this.mfa;
    }

    public String getMfaSecret () {

        return this.mfaSecret;
    }

    public String getAvatarUrl () {

        return this.avatarUrl;
    }
}
