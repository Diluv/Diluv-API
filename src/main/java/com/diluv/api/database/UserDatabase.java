package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.utils.SQLHandler;

public class UserDatabase {

    private static final String FIND_USER_BY_USERNAME = SQLHandler.readFile("user/findUserByUsername");
    private static final String FIND_USERID_BY_USERNAME = SQLHandler.readFile("user/findUserIdByUsername");

    public static UserRecord findOneByUsername (String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_USER_BY_USERNAME)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserRecord(rs);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String findUserIdByUsername (String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_USERID_BY_USERNAME)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
