package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.RefreshTokenRecord;
import com.diluv.api.database.record.TempUserRecord;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.utils.SQLHandler;

public class UserDatabase implements UserDAO {

    private static final String FIND_USERID_BY_EMAIL = SQLHandler.readFile("user/findUserIdByEmail");
    private static final String FIND_USERID_BY_USERNAME = SQLHandler.readFile("user/findUserIdByUsername");
    private static final String FIND_USER_BY_USERNAME = SQLHandler.readFile("user/findUserByUsername");
    private static final String INSERT_USER = SQLHandler.readFile("user/insertUser");

    private static final String EXIST_TEMPUSER_BY_EMAIL = SQLHandler.readFile("temp_user/existTempUserByEmail");
    private static final String EXIST_TEMPUSER_BY_USERNAME = SQLHandler.readFile("temp_user/existTempUserByUsername");
    private static final String INSERT_TEMPUSER = SQLHandler.readFile("temp_user/insertTempUser");
    private static final String FIND_TEMPUSER_BY_EMAIL_AND_USERNAME = SQLHandler.readFile("temp_user/findTempUserByEmailAndUsername");
    private static final String FIND_TEMPUSER_BY_EMAIL_AND_CODE = SQLHandler.readFile("temp_user/findTempUserByEmailAndCode");
    private static final String DELETE_TEMPUSER = SQLHandler.readFile("temp_user/deleteTempUser");

    private static final String INSERT_REFRESHTOKEN = SQLHandler.readFile("user_refresh/insertUserRefresh");
    private static final String FIND_REFRESHTOKEN_BY_USERID_AND_CODE = SQLHandler.readFile("user_refresh/findRefreshTokenByUserIdAndCode");
    private static final String DELETE_REFRESHTOKEN_BY_USERID_AND_CODE = SQLHandler.readFile("user_refresh/deleteRefreshTokenByUserIdAndCode");

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
            DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findUserIdByEmail (String email)", e);
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
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findUserIdByUsername (String username)", e);
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
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findOneByUsername (String username)", e);
        }
        return null;
    }

    @Override
    public boolean insertUser (String email, String username, String password, String passwordType, Timestamp createdAt) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_USER)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, passwordType);
            stmt.setTimestamp(5, createdAt);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "insertUser (String email, String username, String password, String passwordType, Timestamp createdAt)", e);
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
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "existTempUserByEmail (String email)", e);
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
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "existTempUserByUsername (String username)", e);
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
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "insertTempUser (String email, String username, String password, String passwordType, String verificationCode)", e);
        }
        return false;
    }

    @Override
    public TempUserRecord findTempUserByEmailAndUsername (String email, String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_TEMPUSER_BY_EMAIL_AND_USERNAME)) {
            stmt.setString(1, email);
            stmt.setString(2, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TempUserRecord(rs);
                }
            }
        }
        catch (SQLException e) {
        	
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findTempUserByEmailAndUsername (String email, String username)", e);
        }
        return null;
    }

    @Override
    public TempUserRecord findTempUserByEmailAndCode (String email, String code) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_TEMPUSER_BY_EMAIL_AND_CODE)) {
            stmt.setString(1, email);
            stmt.setString(2, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TempUserRecord(rs);
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findTempUserByEmailAndCode (String email, String code)", e);
        }
        return null;
    }

    @Override
    public boolean deleteTempUser (String email, String username) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(DELETE_TEMPUSER)) {
            stmt.setString(1, email);
            stmt.setString(2, username);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "deleteTempUser (String email, String username)", e);
        }
        return false;
    }

    @Override
    public boolean insertRefreshToken (long userId, String code, Timestamp time) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_REFRESHTOKEN)) {
            stmt.setLong(1, userId);
            stmt.setString(2, code);
            stmt.setTimestamp(3, time);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "insertRefreshToken (long userId, String code, Timestamp time)", e);
        }
        return false;
    }

    @Override
    public RefreshTokenRecord findRefreshTokenByUserIdAndCode (Long userId, String code) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_REFRESHTOKEN_BY_USERID_AND_CODE)) {
            stmt.setLong(1, userId);
            stmt.setString(2, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RefreshTokenRecord(rs);
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "findRefreshTokenByUserIdAndCode (Long userId, String code)", e);
        }
        return null;
    }

    @Override
    public boolean deleteRefreshTokenByUserIdAndCode (Long userId, String code) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(DELETE_REFRESHTOKEN_BY_USERID_AND_CODE)) {
            stmt.setLong(1, userId);
            stmt.setString(2, code);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(UserDatabase.class.getName(), "deleteRefreshTokenByUserIdAndCode (Long userId, String code)", e);
        }
        return false;
    }
}