package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.BaseUserRecord;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.utils.SQLHandler;

public class UserDatabase implements UserDAO {

    private static final String FIND_USERID_BY_EMAIL = SQLHandler.readFile("user/findUserIdByEmail");
    private static final String FIND_USERID_BY_USERNAME = SQLHandler.readFile("user/findUserIdByUsername");
    private static final String FIND_USER_BY_USERNAME = SQLHandler.readFile("user/findUserByUsername");
    private static final String INSERT_USER = SQLHandler.readFile("user/insertUser");
    private static final String INSERT_USER_REFRESH = SQLHandler.readFile("user/insertUserRefresh");

    private static final String EXIST_TEMPUSER_BY_EMAIL = SQLHandler.readFile("temp_user/existTempUserByEmail");
    private static final String EXIST_TEMPUSER_BY_USERNAME = SQLHandler.readFile("temp_user/existTempUserByUsername");
    private static final String INSERT_TEMPUSER = SQLHandler.readFile("temp_user/insertTempUser");
    private static final String FIND_TEMPUSER_BY_EMAIL_AND_USERNAME_AND_CODE = SQLHandler.readFile("temp_user/findTempUserByEmailAndUsernameAndCode");

    @Override
    public Long findUserIdByEmail (String email) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_USERID_BY_EMAIL)) {
            stmt.setString(1, email);

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
    public boolean insertUser (String email, String username, String password, String passwordType, String avatar, Timestamp createdAt) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_USER)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, passwordType);
            stmt.setString(5, avatar);
            stmt.setTimestamp(6, createdAt);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertUserRefresh (long userId, String randomKey, Timestamp time) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_USER_REFRESH)) {
            stmt.setLong(1, userId);
            stmt.setString(2, randomKey);
            stmt.setTimestamp(3, time);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existTempUserByEmail (String email) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(EXIST_TEMPUSER_BY_EMAIL)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean existTempUserByUsername (String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(EXIST_TEMPUSER_BY_USERNAME)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean insertTempUser (String email, String username, String password, String passwordType, String verificationCode) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_TEMPUSER)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, passwordType);
            stmt.setString(5, verificationCode);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BaseUserRecord findTempUserByEmailAndUsernameAndCode (String email, String username, String code) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_TEMPUSER_BY_EMAIL_AND_USERNAME_AND_CODE)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BaseUserRecord(rs);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}