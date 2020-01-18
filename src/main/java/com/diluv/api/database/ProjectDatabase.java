package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.record.ProjectFileRecord;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.database.record.ProjectTypeRecord;
import com.diluv.api.utils.SQLHandler;

public class ProjectDatabase implements ProjectDAO {

    private static final String INSERT_PROJECT = SQLHandler.readFile("project/insertProject");
    private static final String FIND_ALL_BY_USERID = SQLHandler.readFile("project/findAllByUserId");
    private static final String FIND_ALL_BY_GAMESLUG_AND_PROJECTYPESLUG = SQLHandler.readFile("project/findAllByGameSlugAndProjectTypeSlug");
    private static final String FIND_ONE_BY_GAMESLUG_AND_PROJECTYPESLUG_AND_PROJECTSLUG = SQLHandler.readFile("project/findOneByGameSlugAndProjectTypeSlugAndProjectSlug");

    private static final String FIND_ONE_PROJECTTYPES_BY_GAMESLUG_AND_PROJECTYPESLUG = SQLHandler.readFile("project_types/findOneByGameSlugAndProjectTypeSlug");
    private static final String FIND_ALL_PROJECTTYPES_BY_GAMESLUG = SQLHandler.readFile("project_types/findAllByGameSlug");

    private static final String FIND_ALL_PROJECTFILES_BY_GAMESLUG_AND_PROJECTYPESLUG_AND_PROJECTSLUG = SQLHandler.readFile("project_files/findAllByGameSlugAndProjectTypeAndProjectSlug");


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
        	DiluvAPI.LOGGER.error("Failed to run findAllByUserId database script for user {}.", userId, e);
        }
        return projects;
    }

    @Override
    public List<ProjectTypeRecord> findAllProjectTypesByGameSlug (String gameSlug) {

        List<ProjectTypeRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_PROJECTTYPES_BY_GAMESLUG)) {
            stmt.setString(1, gameSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new ProjectTypeRecord(rs));
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run findAllProjectTypesByGameSlug database script for game slug {}.", gameSlug, e);
        }
        return projects;
    }

    @Override
    public List<ProjectRecord> findAllProjectsByGameSlugAndProjectType (String gameSlug, String projectTypeSlug) {

        List<ProjectRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_BY_GAMESLUG_AND_PROJECTYPESLUG)) {
            stmt.setString(1, gameSlug);
            stmt.setString(2, projectTypeSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new ProjectRecord(rs));
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run findAllProjectsByGameSlugAndProjectType script for game {} and type {}.", gameSlug, projectTypeSlug, e);
        }
        return projects;
    }

    @Override
    public ProjectTypeRecord findOneProjectTypeByGameSlugAndProjectTypeSlug (String gameSlug, String projectTypeSlug) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ONE_PROJECTTYPES_BY_GAMESLUG_AND_PROJECTYPESLUG)) {
            stmt.setString(1, gameSlug);
            stmt.setString(2, projectTypeSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProjectTypeRecord(rs);
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run findOneProjectTypeByGameSlugAndProjectTypeSlug script for game {} and type {}.", gameSlug, projectTypeSlug, e);
        }
        return null;
    }

    @Override
    public ProjectRecord findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ONE_BY_GAMESLUG_AND_PROJECTYPESLUG_AND_PROJECTSLUG)) {
            stmt.setString(1, gameSlug);
            stmt.setString(2, projectTypeSlug);
            stmt.setString(3, projectSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProjectRecord(rs);
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug script for game {}, type {}, and project {}.", gameSlug, projectTypeSlug, projectSlug, e);
        }
        return null;
    }

    @Override
    public List<ProjectFileRecord> findAllProjectFilesByGameSlugAndProjectType (String gameSlug, String projectTypeSlug, String projectSlug) {

        //TODO Add boolean param to include/exclude non-public files
        List<ProjectFileRecord> projects = new ArrayList<>();
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(FIND_ALL_PROJECTFILES_BY_GAMESLUG_AND_PROJECTYPESLUG_AND_PROJECTSLUG)) {
            stmt.setString(1, gameSlug);
            stmt.setString(2, projectTypeSlug);
            stmt.setString(3, projectSlug);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new ProjectFileRecord(rs));
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run findAllProjectFilesByGameSlugAndProjectType script for game {}, type {}, and project {}.", gameSlug, projectTypeSlug, projectSlug, e);
        }
        return projects;
    }

    @Override
    public boolean insertProject (String slug, String name, String summary, String description, long userId, String gameSlug, String projectTypeSlug) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(INSERT_PROJECT)) {
            stmt.setString(1, slug);
            stmt.setString(2, name);
            stmt.setString(3, summary);
            stmt.setString(4, description);
            stmt.setLong(5, userId);
            stmt.setString(6, gameSlug);
            stmt.setString(7, projectTypeSlug);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.error("Failed to run insertProject script for project {} with name {} by {}.", slug, name, userId, e);
        }
        return false;
    }
}
