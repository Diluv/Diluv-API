package com.diluv.api.database.dao;

import java.sql.Timestamp;

import com.diluv.api.database.record.TempUserRecord;
import com.diluv.api.database.record.UserRecord;

public interface UserDAO {

    Long findUserIdByEmail (String email);

    Long findUserIdByUsername (String username);

    UserRecord findOneByUsername (String username);

    boolean insertUser (String email, String username, String password, String passwordType,Timestamp createdAt);

    boolean insertUserRefresh (long userId, String randomKey, Timestamp time);

    boolean existTempUserByEmail (String email);

    boolean existTempUserByUsername (String username);

    boolean insertTempUser (String email, String username, String password, String passwordType, String verificationCode);

    TempUserRecord findTempUserByEmailAndUsername(String email, String username);

    TempUserRecord findTempUserByEmailAndCode (String email, String code);

    boolean deleteTempUser (String email, String username);
}
