package com.diluv.api.database;

import java.sql.Timestamp;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.RefreshTokenRecord;
import com.diluv.confluencia.database.record.TempUserRecord;
import com.diluv.confluencia.database.record.UserRecord;

public class UserTestDatabase implements UserDAO {

    private final List<UserRecord> userList;
    private final List<TempUserRecord> tempUsersList;
    private final List<RefreshTokenRecord> refreshTokens;

    public UserTestDatabase () {

        this.userList = FileReader.readJsonFolder("users", UserRecord.class);
        this.tempUsersList = FileReader.readJsonFolder("temp_users", TempUserRecord.class);
        this.refreshTokens = FileReader.readJsonFolder("refresh_token", RefreshTokenRecord.class);
    }

    @Override
    public boolean existsUserByEmail (String email) {

        for (UserRecord userRecord : userList) {
            if (userRecord.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean existsUserByUsername (String username) {

        for (UserRecord userRecord : userList) {
            if (userRecord.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public UserRecord findOneByUsername (String username) {

        for (UserRecord userRecord : userList) {
            if (userRecord.getUsername().equals(username)) {
                return userRecord;
            }
        }

        return null;
    }

    @Override
    public boolean insertUser (String email, String username, String password, String passwordType, Timestamp timestamp) {

        return true;
    }

    @Override
    public boolean existsTempUserByEmail (String email) {

        for (TempUserRecord userRecord : this.tempUsersList) {
            if (userRecord.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean existsTempUserByUsername (String username) {

        for (TempUserRecord userRecord : this.tempUsersList) {
            if (userRecord.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean insertTempUser (String email, String username, String password, String passwordType, String verificationCode) {

        return true;
    }

    @Override
    public boolean updateTempUser (String email, String username, String verificationCode) {

        return true;
    }

    @Override
    public TempUserRecord findTempUserByEmailAndUsername (String email, String username) {

        for (TempUserRecord record : this.tempUsersList) {
            if (record.getEmail().equalsIgnoreCase(email) &&
                record.getUsername().equalsIgnoreCase(username)) {
                return record;
            }
        }
        return null;
    }

    @Override
    public TempUserRecord findTempUserByEmailAndCode (String email, String code) {

        for (TempUserRecord record : this.tempUsersList) {
            if (record.getEmail().equalsIgnoreCase(email) &&
                record.getVerificationCode().equalsIgnoreCase(code)) {
                return record;
            }
        }
        return null;
    }

    @Override
    public boolean deleteTempUser (String email, String username) {

        return true;
    }

    @Override
    public boolean insertRefreshToken (long userId, String code, Timestamp time) {

        return true;
    }

    @Override
    public RefreshTokenRecord findRefreshTokenByUserIdAndCode (long userId, String code) {

        for (RefreshTokenRecord record : this.refreshTokens) {
            if (record.getCode().equalsIgnoreCase(code) && record.getUserId() == userId) {
                return record;
            }
        }
        return null;
    }

    @Override
    public boolean deleteRefreshTokenByUserIdAndCode (long userId, String code) {

        return true;
    }
}
