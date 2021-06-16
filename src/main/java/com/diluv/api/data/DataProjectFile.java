package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a file uploaded to a project.
 */
public class DataProjectFile {

    /**
     * An internal id for the file.
     */
    @Expose
    private final long id;

    /**
     * The display name of the file.
     */
    @Expose
    private final String name;

    /**
     * The download url for the file
     */
    @Expose
    private final String downloadURL;

    /**
     * The byte size of the file.
     */
    @Expose
    private final long size;

    /**
     * The changelog or description for the file.
     */
    @Expose
    private final String changelog;

    /**
     * The SHA-512 hash of the file.
     */
    @Expose
    private final String sha512;

    // TODO
    @Expose
    private final long downloads;

    /**
     * The release type of the file. E.g. release, beta, alpha
     */
    @Expose
    private final String releaseType;

    /**
     * The type of file. E.g. binary, deobf, javadocs.
     */
    @Expose
    private final String classifier;

    /**
     * The time when the file was created.
     */
    @Expose
    private final long createdAt;

    /**
     * The list of the project ids the file depends on.
     */
    @Expose
    private final List<DataProjectFileDependency> dependencies;

    /**
     * The list of game versions the file works with
     */
    @Expose
    private final List<DataGameVersion> gameVersions;

    /**
     * The slug of the game the project belongs to.
     */
    @Expose
    private final String gameSlug;

    /**
     * The slug of the project's type.
     */
    @Expose
    private final String projectTypeSlug;

    /**
     * The slug of the project.
     */
    @Expose
    private final String projectSlug;

    /**
     * The user who uploaded the file
     */
    @Expose
    private final DataUser user;

    public DataProjectFile (ProjectFilesEntity rs, String gameSlug, String projectTypeSlug, String projectSlug) {

        this.id = rs.getId();
        this.name = rs.getDisplayName();
        this.downloadURL = Constants.getFileURL(gameSlug, projectTypeSlug, rs.getProject().getId(), rs.getId(), rs.getDisplayName());
        this.size = rs.getSize();
        this.changelog = rs.getChangelog();
        this.sha512 = rs.getSha512();
        this.downloads = rs.getDownloads();
        this.releaseType = rs.getReleaseType();
        this.classifier = rs.getClassifier();
        this.createdAt = rs.getCreatedAt().getTime();
        this.user = new DataUser(rs.getUser());
        this.dependencies = rs.getDependencies().stream().map(DataProjectFileDependency::new).collect(Collectors.toList());
        this.gameVersions = rs.getGameVersions().stream().map(a -> new DataGameVersion(a.getGameVersion())).collect(Collectors.toList());
        this.gameSlug = gameSlug;
        this.projectTypeSlug = projectTypeSlug;
        this.projectSlug = projectSlug;
    }
}