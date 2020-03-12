package com.diluv.api.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.record.FileProcessingStatus;
import com.diluv.confluencia.database.record.GameVersionRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;

public class FileTestDatabase implements FileDAO {

    private final List<ProjectFileRecord> projectFileRecords;
    private long x = 0;

    public FileTestDatabase () {

        this.projectFileRecords = FileReader.readJsonFolder("project_files", ProjectFileRecord.class);
    }

    @Override
    public boolean updateStatusById (FileProcessingStatus status, long id) throws SQLException {

        return true;
    }

    @Override
    public boolean updateStatusByStatus (FileProcessingStatus set, FileProcessingStatus where) {

        return true;
    }

    @Override
    public List<ProjectFileRecord> findAllWhereStatusAndLimit (FileProcessingStatus status, int amount) {

        // TODO
        return new ArrayList<>();
    }

    @Override
    public List<ProjectFileRecord> getLatestFiles (int amount) throws SQLException {

        // TODO
        return new ArrayList<>();
    }

    @Override
    public List<ProjectFileRecord> findAllByGameSlugAndProjectTypeAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug, long page, int limit) {

        // ProjectRecord project =
        // this.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug,
        // projectTypeSlug, projectSlug);
        // if (project == null) {
        // return null;
        // }
        // return this.projectFileRecords.stream().filter(projectRecord ->
        // projectRecord.getProjectId() == projectSlug).collect(Collectors.toList());
        // TODO
        return new ArrayList<>();
    }

    @Override
    public List<ProjectFileRecord> findAllByGameSlugAndProjectTypeAndProjectSlugAuthorized (String gameSlug, String projectTypeSlug, String projectSlug, long page, int limit) {

        // TODO
        return this.projectFileRecords;
    }

    @Override
    public boolean insertProjectFileAntivirus (long projectId, String malware) {

        return true;
    }

    @Override
    public List<GameVersionRecord> findAllGameVersionsByProjectFile (long projectFileId) {

        return new ArrayList<>();
    }

    @Override
    public Long insertProjectFile (String name, long size, String changelog, String sha512, String releaseType, String classifier, long projectId, long userId) {

        x++;
        return (long) this.projectFileRecords.size() + x;
    }

    @Override
    public ProjectFileRecord findOneProjectFileQueueByFileId (long fileId) {

        return this.projectFileRecords.get(0);
    }
}
