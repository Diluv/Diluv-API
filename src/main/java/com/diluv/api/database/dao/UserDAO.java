package com.diluv.api.database.dao;

import java.sql.Timestamp;

import com.diluv.api.database.record.UserRecord;

public interface UserDAO {

    Long findUserIdByEmail (String email);

    Long findUserIdByUsername (String username);

    UserRecord findOneByUsername (String username);

    boolean existTempUserByEmail (String email);

    boolean existTempUserByUsername (String username);

    boolean insertTempUser (String username, String email, String password, String passwordType, String avatar, String verificationCode);

    boolean insertUserRefresh (long userId, String randomKey, Timestamp time);
}
