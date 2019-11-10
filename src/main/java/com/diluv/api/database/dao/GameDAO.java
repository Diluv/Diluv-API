package com.diluv.api.database.dao;

import com.diluv.api.database.record.GameRecord;

import java.util.List;

public interface GameDAO {

    List<GameRecord> findAll ();

    GameRecord findOneBySlug (String name);
}
