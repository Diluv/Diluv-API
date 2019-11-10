package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.utils.SQLHandler;

public class UserDatabase implements UserDAO {

    private static final String FIND_USER_BY_USERNAME = SQLHandler.readFile("user/findUserByUsername");
    private static final String FIND_USERID_BY_USERNAME = SQLHandler.readFile("user/findUserIdByUsername");

    @Override
    public UserRecord findOneByUsername (String username) {

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

    @Override
    public Long findUserIdByUsername (String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_USERID_BY_USERNAME)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
