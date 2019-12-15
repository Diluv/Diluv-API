package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BaseUserRecord {
    private long id;
    private String username;
    private String email;
    private String password;
    private String passwordType;
    private Timestamp createdAt;

    public BaseUserRecord () {

    }

    public BaseUserRecord (ResultSet rs) throws SQLException {

        this.id = rs.getLong("id");
        this.username = rs.getString("username");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
        this.passwordType = rs.getString("password_type");
        this.createdAt = rs.getTimestamp("created_at");
    }

    public BaseUserRecord (long id, String email, String username, String password, String passwordType, Timestamp createdAt) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordType = passwordType;
        this.createdAt = createdAt;
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

    public Timestamp getCreatedAt () {

        return this.createdAt;
    }
}
