package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.record.GameRecord;
import com.diluv.api.utils.SQLHandler;

public class GameDatabase implements GameDAO {
    private static final String FIND_ALL = SQLHandler.readFile("game/findAll");
    private static final String FIND_ONE_BY_SLUG = SQLHandler.readFile("game/findOneBySlug");

    @Override
    public List<GameRecord> findAll () {

        List<GameRecord> gameRecords = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    gameRecords.add(new GameRecord(rs));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return gameRecords;
    }

    @Override
    public GameRecord findOneBySlug (String name) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ONE_BY_SLUG)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameRecord(rs);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
