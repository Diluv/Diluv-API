package com.diluv.api.database;

import java.sql.Timestamp;
import java.util.List;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.utils.FileReader;

public class UserTestDatabase implements UserDAO {

    private final List<UserRecord> userList;

    public UserTestDatabase () {

        this.userList = FileReader.readJsonFolder("users", UserRecord.class);
    }

    @Override
    public Long findUserIdByEmail (String email) {

        for (UserRecord userRecord : userList) {
            if (userRecord.getEmail().equals(email)) {
                return userRecord.getId();
            }
        }

        return null;
    }

    @Override
    public Long findUserIdByUsername (String username) {

        for (UserRecord userRecord : userList) {
            if (userRecord.getUsername().equals(username)) {
                return userRecord.getId();
            }
        }

        return null;
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
    public boolean insertUser (String email, String username, String password, String passwordType, String avatar, Timestamp timestamp) {
        // TODO
        return false;
    }

    @Override
    public boolean insertUserRefresh (long userId, String randomKey, Timestamp time) {
// TODO
        return false;
    }

    @Override
    public boolean existTempUserByEmail (String email) {
// TODO
        return false;
    }

    @Override
    public boolean existTempUserByUsername (String username) {
// TODO
        return false;
    }

    @Override
    public boolean insertTempUser (String email, String username, String password, String passwordType, String verificationCode) {
// TODO
        return false;
    }

    @Override
    public UserRecord findTempUserByEmailAndUsernameAndCode (String email, String username, String code) {
// TODO
        return null;
    }
}
