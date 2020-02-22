package com.diluv.api.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.record.FileStatus;
import com.diluv.confluencia.database.record.ProjectFileRecord;

public class FileTestDatabase implements FileDAO {
    
    private final List<ProjectFileRecord> projectFileRecords;
    
    public FileTestDatabase() {
        
        this.projectFileRecords = FileReader.readJsonFolder("project_files", ProjectFileRecord.class);
    }
    
    @Override
    public List<ProjectFileRecord> findAllWhereStatusAndLimit (FileStatus status, int amount) {
        
        // TODO
        return new ArrayList<>();
    }
    
    @Override
    public boolean updateStatusById (long id, FileStatus status) throws SQLException {
        
        return true;
    }
    
    @Override
    public List<ProjectFileRecord> getLatestFiles (int amount) throws SQLException {
        
        // TODO
        return new ArrayList<>();
    }
    
    @Override
    public List<ProjectFileRecord> findAllByGameSlugAndProjectTypeAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug) {
        
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
    public List<ProjectFileRecord> findAllByGameSlugAndProjectTypeAndProjectSlugAuthorized (String gameSlug, String projectTypeSlug, String projectSlug) {
        
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
