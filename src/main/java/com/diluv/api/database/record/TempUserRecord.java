package com.diluv.api.database.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TempUserRecord extends BaseUserRecord {
    private String verificationCode;

    public TempUserRecord () {

    }

    public TempUserRecord (ResultSet rs) throws SQLException {

        super(rs);
        this.verificationCode = rs.getString("verificationCode");
    }

    @JsonCreator
    public TempUserRecord (long id, String email, String username, String password, String passwordType, Timestamp createdAt, String verificationCode) {

        super(id, email, username, password, passwordType, createdAt);
        this.verificationCode = verificationCode;
    }

    public String getVerificationCode () {

        return this.verificationCode;
    }
}
