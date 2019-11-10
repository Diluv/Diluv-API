package com.diluv.api.database;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.UserRecord;

public class UserTestDatabase implements UserDAO {

    @Override
    public Long findUserIdByUsername (String username) {

        return 1L;
    }

    @Override
    public UserRecord findOneByUsername (String username) {

        if (username.equalsIgnoreCase("testuser")) {
            return new UserRecord(1L, "testuser", "test@example.com", "bcrypt", false, null, "https://via.placeholder.com/150");
        }
        else {
            return null;
        }
    }
}
