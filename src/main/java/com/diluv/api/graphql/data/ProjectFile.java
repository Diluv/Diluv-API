package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.ProjectFilesEntity;

public class ProjectFile {

    private final long id;
    private final String name;
    private final long size;
    private final String changelog;
    private final String sha512;
    private final long downloads;
    private final String releaseType;
    private final String classifier;
    private final String createdAt;
    private final boolean released;

    private final ProjectFilesEntity entity;

    public ProjectFile (ProjectFilesEntity rs) {

        this.id = rs.getId();
        this.name = rs.getDisplayName();
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.sha512 = rs.getSha512();
        this.downloads = rs.getDownloads();
        this.releaseType = rs.getReleaseType();
        this.classifier = rs.getClassifier();
        this.createdAt = rs.getCreatedAt().toString();
        this.released = rs.isReleased();

        this.entity = rs;
    }

    public ProjectFilesEntity getEntity () {

        return entity;
    }
}
