package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.utils.SQLHandler;

public class ProjectDatabase implements ProjectDAO {

    private static final String FIND_ALL_BY_USERID = SQLHandler.readFile("project/findAllByUserId");
    private static final String FIND_ALL_BY_GAMESLUG = SQLHandler.readFile("project/findAllByGameSlug");

    @Override
    public List<ProjectRecord> findAllByUserId (long userId) {

        List<ProjectRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_BY_USERID)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new ProjectRecord(rs));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    @Override
    public List<ProjectRecord> findAllByGameSlug (String gameSlug) {

        List<ProjectRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_BY_GAMESLUG)) {
            stmt.setString(1, gameSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new ProjectRecord(rs));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
}
