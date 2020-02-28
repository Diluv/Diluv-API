package com.diluv.api.database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.utils.FileReader;
import com.diluv.api.utils.TestUtil;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.diluv.confluencia.database.record.UserRecord;

public class ProjectTestDatabase implements ProjectDAO {

    private final List<ProjectRecord> projectRecords;
    private final List<ProjectTypeRecord> projectTypeRecords;

    public ProjectTestDatabase () {

        this.projectRecords = FileReader.readJsonFolder("projects", ProjectRecord.class);
        this.projectTypeRecords = FileReader.readJsonFolder("project_types", ProjectTypeRecord.class);
    }

    @Override
    public List<ProjectRecord> findAllByUsername (String username) {

        final UserRecord user = TestUtil.USER_DAO.findOneByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        return this.projectRecords.stream().filter(projectRecord -> projectRecord.getUserId() == user.getId() && projectRecord.isReleased()).collect(Collectors.toList());
    }

    @Override
    public List<ProjectRecord> findAllByUsernameWhereAuthorized (String username) {

        final UserRecord user = TestUtil.USER_DAO.findOneByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        return this.projectRecords.stream().filter(projectRecord -> projectRecord.getUserId() == user.getId()).collect(Collectors.toList());
    }

    @Override
    public List<ProjectTypeRecord> findAllProjectTypesByGameSlug (String gameSlug) {

        return this.projectTypeRecords.stream().filter(projectRecord -> projectRecord.getGameSlug().equals(gameSlug)).collect(Collectors.toList());
    }

    @Override
    public List<ProjectRecord> findAllProjectsByGameSlugAndProjectType (String gameSlug, String projectTypeSlug) {

        return this.projectRecords.stream().filter(projectRecord -> projectRecord.getGameSlug().equals(gameSlug) && projectRecord.getProjectTypeSlug().equals(projectTypeSlug)).collect(Collectors.toList());
    }

    @Override
    public ProjectTypeRecord findOneProjectTypeByGameSlugAndProjectTypeSlug (String gameSlug, String projectTypeSlug) {

        return this.projectTypeRecords.stream().filter(projectRecord -> projectRecord.getGameSlug().equals(gameSlug) && projectRecord.getSlug().equals(projectTypeSlug)).findAny().orElse(null);
    }

    @Override
    public ProjectRecord findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug) {

        return this.projectRecords.stream().filter(projectRecord -> projectRecord.getGameSlug().equals(gameSlug) && projectRecord.getProjectTypeSlug().equals(projectTypeSlug) && projectRecord.getSlug().equals(projectSlug)).findAny().orElse(null);
    }

    @Override
    public List<ProjectAuthorRecord> findAllProjectAuthorsByProjectId (long projectId) {

        return new ArrayList<>();
    }

    @Override
    public boolean insertProject (String slug, String name, String summary, String description, long userId, String gameSlug, String projectTypeSlug) {

        this.projectRecords.add(new ProjectRecord(this.projectRecords.size(), name, slug, summary, description, 0L, System.currentTimeMillis(), System.currentTimeMillis(), gameSlug, projectTypeSlug, false, true, userId, "lclc98", System.currentTimeMillis()));
        return true;
    }

    @Override
    public ProjectRecord findOneProjectByProjectId (long id) {

        return this.projectRecords.stream().filter(projectRecord -> projectRecord.getId() == id).findAny().orElse(null);
    }
}
