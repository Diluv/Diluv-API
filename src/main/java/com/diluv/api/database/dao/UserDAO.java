package com.diluv.api.database.dao;

import com.diluv.api.database.record.UserRecord;

public interface UserDAO {

    Long findUserIdByUsername (String username);

    UserRecord findOneByUsername (String username);
}
