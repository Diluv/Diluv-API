package com.diluv.api.database.dao;

import java.util.List;

import com.diluv.api.database.record.ProjectRecord;

public interface ProjectDAO {

    List<ProjectRecord> findAllByUserId (long userId);

    List<ProjectRecord> findAllByGameSlug (String gameSlug);
}
