package com.diluv.api.database.dao;

import java.util.List;

import com.diluv.api.database.record.ProjectFileRecord;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.database.record.ProjectTypeRecord;

public interface ProjectDAO {

    List<ProjectRecord> findAllByUserId (long userId);

    List<ProjectTypeRecord> findAllProjectTypesByGameSlug (String gameSlug);

    List<ProjectRecord> findAllProjectsByGameSlugAndProjectType (String gameSlug, String projectTypeSlug);

    ProjectTypeRecord findOneProjectTypeByGameSlugAndProjectTypeSlug (String gameSlug, String projectTypeSlug);

    ProjectRecord findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug (String gameSlug, String projectTypeSlug, String projectSlug);

    List<ProjectFileRecord> findAllProjectFilesByGameSlugAndProjectType (String gameSlug, String projectTypeSlug, String projectSlug);
}
