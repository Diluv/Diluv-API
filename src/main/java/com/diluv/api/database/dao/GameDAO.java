package com.diluv.api.database.dao;

import java.util.List;

import com.diluv.api.database.record.GameRecord;

public interface GameDAO {

    List<GameRecord> findAll ();

    GameRecord findOneBySlug (String slug);
}
