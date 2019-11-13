package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRecord {
    private long id;
    private String username;
    private String email;
    private String password;
    private String passwordType;
    private boolean mfa;
    private String mfaSecret;
    private String avatarUrl;

    public UserRecord () {

    }

    public UserRecord (ResultSet rs) throws SQLException {

        this.id = rs.getLong("id");
        this.username = rs.getString("username");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
        this.passwordType = rs.getString("password_type");
        this.mfa = rs.getBoolean("mfa");
        this.mfaSecret = rs.getString("mfa_secret");
        this.avatarUrl = rs.getString("avatar_url");
    }

    public UserRecord (long id, String username, String email, String password, String passwordType, boolean mfa, String mfaSecret, String avatarUrl) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordType = passwordType;
        this.mfa = mfa;
        this.mfaSecret = mfaSecret;
        this.avatarUrl = avatarUrl;
    }

    public long getId () {

        return this.id;
    }

    public String getUsername () {

        return this.username;
    }

    public String getEmail () {

        return this.email;
    }

    public String getPassword () {

        return this.password;
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
