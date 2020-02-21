package com.diluv.api.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.record.ProjectFileRecord;

public class FileTestDatabase implements FileDAO {

    private final List<ProjectFileRecord> projectFileRecords;

    public FileTestDatabase () {

        this.projectFileRecords = FileReader.readJsonFolder("project_files", ProjectFileRecord.class);
    }

    @Override
    public List<ProjectFileRecord> findAllWherePending (int amount) {

        //TODO
        return new ArrayList<>();
    }

    @Override
    public boolean updateFileQueueStatusById (long id) throws SQLException {

        return true;
    }

    @Override
    public List<ProjectFileRecord> getLatestFileQueueRecord (int amount) throws SQLException {

        //TODO
        return new ArrayList<>();
    }

    @Override
    public List<ProjectFileRecord> findAllProjectFilesByGameSlugAndProjectTypeAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug) {

//        ProjectRecord project = this.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
//        if (project == null) {
//            return null;
//        }
//        return this.projectFileRecords.stream().filter(projectRecord -> projectRecord.getProjectId() == projectSlug).collect(Collectors.toList());
        //TODO
        return new ArrayList<>();
    }

    @Override
    public List<ProjectFileRecord> findAllProjectFilesByGameSlugAndProjectTypeAndProjectSlugAuthorized (String gameSlug, String projectTypeSlug, String projectSlug) {

        // TODO
        return this.projectFileRecords;
    }

    @Override
    public boolean insertProjectFileAntivirus (long projectId, String malware) {

        return true;
    }

    @Override
    public Long insertProjectFile (String name, long size, String changelog, String sha512, long projectId, long userId) {

        return (long) this.projectFileRecords.size();
    }

    @Override
    public ProjectFileRecord findOneProjectFileQueueByFileId (long fileId) {

        return this.projectFileRecords.get(0);
    }
}
