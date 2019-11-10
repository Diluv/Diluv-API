package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.utils.SQLHandler;

public class ProjectDatabase {

    private static final String FIND_ALL_BY_USERID = SQLHandler.readFile("project/findAllByUserId");

    public static List<ProjectRecord> findAllByUserId (String userId) {

        List<ProjectRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_BY_USERID)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);

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
