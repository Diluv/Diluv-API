package com.diluv.api.database.dao;

public interface EmailDAO {

    boolean existsBlacklist (String email, String domain);
}
