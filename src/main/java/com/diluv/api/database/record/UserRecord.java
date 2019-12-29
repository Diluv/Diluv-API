package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserRecord extends BaseUserRecord {
    private boolean mfa;
    private String mfaSecret;

    public UserRecord () {

    }

    public UserRecord (ResultSet rs) throws SQLException {

        super(rs);
        this.mfa = rs.getBoolean("mfa");
        this.mfaSecret = rs.getString("mfa_secret");
    }

    public UserRecord (long id, String email, String username, String password, String passwordType, boolean mfa, String mfaSecret, Timestamp createdAt) {

        super(id, email, username, password, passwordType, createdAt);
        this.mfa = mfa;
        this.mfaSecret = mfaSecret;
    }

    public boolean isMfa () {

        return this.mfa;
    }

    public String getMfaSecret () {

        return this.mfaSecret;
    }
}
