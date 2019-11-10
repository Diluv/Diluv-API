package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRecord {
    private final String id;
    private final String username;
    private final String email;
    private final String passwordType;
    private final boolean mfa;
    private final String mfaSecret;
    private final String avatarUrl;

    public UserRecord (ResultSet rs) throws SQLException {

        this.id = rs.getString("id");
        this.username = rs.getString("username");
        this.email = rs.getString("email");
        this.passwordType = rs.getString("password_type");
        this.mfa = rs.getBoolean("mfa");
        this.mfaSecret = rs.getString("mfa_secret");
        this.avatarUrl = rs.getString("avatar_url");
    }

    public String getId () {

        return this.id;
    }

    public String getUsername () {

        return this.username;
    }

    public String getEmail () {

        return this.email;
    }

    public String getPasswordType () {

        return this.passwordType;
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
