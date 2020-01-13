package com.diluv.api.database.dao;

import java.sql.Timestamp;

import com.diluv.api.database.record.RefreshTokenRecord;
import com.diluv.api.database.record.TempUserRecord;
import com.diluv.api.database.record.UserRecord;

public interface UserDAO {

    Long findUserIdByEmail (String email);

    Long findUserIdByUsername (String username);

    UserRecord findOneByUsername (String username);

    boolean insertUser (String email, String username, String password, String passwordType, Timestamp createdAt);

    boolean existTempUserByEmail (String email);

    boolean existTempUserByUsername (String username);

    boolean insertTempUser (String email, String username, String password, String passwordType, String verificationCode);

    TempUserRecord findTempUserByEmailAndUsername (String email, String username);

    TempUserRecord findTempUserByEmailAndCode (String email, String code);

    boolean deleteTempUser (String email, String username);

    boolean insertRefreshToken (long userId, String code, Timestamp time);

    RefreshTokenRecord findRefreshTokenByUserIdAndCode (Long userId, String code);

    boolean deleteRefreshTokenByUserIdAndCode (Long userId, String code);
}
