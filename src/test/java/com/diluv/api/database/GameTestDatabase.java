package com.diluv.api.database;

import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.record.GameRecord;

import java.util.List;

public class GameTestDatabase implements GameDAO {

    @Override
    public List<GameRecord> findAll () {

        return null;
    }

    @Override
    public GameRecord findOneBySlug (String name) {

        return null;
    }
}
