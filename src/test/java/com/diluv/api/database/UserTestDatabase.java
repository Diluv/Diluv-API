package com.diluv.api.database;

import java.sql.Timestamp;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.APITokenRecord;
import com.diluv.confluencia.database.record.RefreshTokenRecord;
import com.diluv.confluencia.database.record.TempUserRecord;
import com.diluv.confluencia.database.record.UserRecord;

public class UserTestDatabase implements UserDAO {

    private final List<UserRecord> userList;
    private final List<TempUserRecord> tempUsersList;
    private final List<RefreshTokenRecord> refreshTokens;
    private final List<APITokenRecord> apiTokens;

    public UserTestDatabase () {

        this.userList = FileReader.readJsonFolder("users", UserRecord.class);
        this.tempUsersList = FileReader.readJsonFolder("temp_users", TempUserRecord.class);
        this.refreshTokens = FileReader.readJsonFolder("refresh_token", RefreshTokenRecord.class);
        this.apiTokens = FileReader.readJsonFolder("api_token", APITokenRecord.class);
    }

    @Override
    public boolean existsUserByEmail (String email) {

        for (final UserRecord userRecord : this.userList) {
            if (userRecord.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean existsUserByUsername (String username) {

        for (final UserRecord userRecord : this.userList) {
            if (userRecord.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public UserRecord findOneByUsername (String username) {

        for (final UserRecord userRecord : this.userList) {
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

        for (final TempUserRecord userRecord : this.tempUsersList) {
            if (userRecord.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean existsTempUserByUsername (String username) {

        for (final TempUserRecord userRecord : this.tempUsersList) {
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

        for (final TempUserRecord record : this.tempUsersList) {
            if (record.getEmail().equalsIgnoreCase(email) && record.getUsername().equalsIgnoreCase(username)) {
                return record;
            }
        }
        return null;
    }

    @Override
    public TempUserRecord findTempUserByEmailAndCode (String email, String code) {

        for (final TempUserRecord record : this.tempUsersList) {
            if (record.getEmail().equalsIgnoreCase(email) && record.getVerificationCode().equalsIgnoreCase(code)) {
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

        for (final RefreshTokenRecord record : this.refreshTokens) {
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

    @Override
    public boolean insertAPITokens (long userId, String code, String name) {

        return true;
    }

    @Override
    public boolean insertAPITokenPermissions (long userId, String code, List<String> permissions) {

        return true;
    }

    @Override
    public APITokenRecord findAPITokenByUserIdAndCode (long userId, String code) {

        for (final APITokenRecord record : this.apiTokens) {
            if (record.getCode().equalsIgnoreCase(code) && record.getUserId() == userId) {
                return record;
            }
        }
        return null;
    }

    @Override
    public boolean deleteAPITokenByUserIdAndCode (long userId, String code) {

        return true;
    }
}
