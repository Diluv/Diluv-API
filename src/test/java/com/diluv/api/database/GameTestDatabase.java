package com.diluv.api.database;

import java.util.Collections;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.filter.GameFilter;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.GameVersionRecord;

public class GameTestDatabase implements GameDAO {

    private final List<GameRecord> gameRecords;

    public GameTestDatabase () {

        this.gameRecords = FileReader.readJsonFolder("games", GameRecord.class);
    }

    @Override
    public List<GameRecord> findAll (GameFilter filter) {

        return this.gameRecords;
    }

    @Override
    public List<GameVersionRecord> findAllGameVersionsByGameSlug (String gameSlug) {

        return Collections.emptyList();
    }

    @Override
    public GameRecord findOneBySlug (String slug) {

        for (final GameRecord userRecord : this.gameRecords) {
            if (userRecord.getSlug().equals(slug)) {
                return userRecord;
            }
        }
        return null;
    }

    @Override
    public List<GameRecord> findFeaturedGames () {

        return Collections.singletonList(this.gameRecords.get((int) (Math.random() * this.gameRecords.size())));
    }
}
