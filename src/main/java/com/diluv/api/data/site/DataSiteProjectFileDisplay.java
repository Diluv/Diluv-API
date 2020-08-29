package com.diluv.api.data.site;

import com.diluv.api.data.DataGameVersion;
import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Represents a file uploaded to a project.
 */
public class DataSiteProjectFileDisplay {

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

    // TODO
    @Expose
    private final String changelog;

    /**
     * The time when the file was created.
     */
    @Expose
    private final long createdAt;

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
     * The user id who uploaded the file
     */
    @Expose
    private final long uploaderUserId;

    /**
     * The username who uploaded the file
     */
    @Expose
    private final String uploaderUsername;

    @Expose
    private final String uploaderDisplayName;

    public DataSiteProjectFileDisplay (ProjectFilesEntity rs, List<DataGameVersion> gameVersions) {

        this.id = rs.getId();
        this.name = rs.getName();
        this.gameSlug = rs.getProject().getGame().getSlug();
        this.projectTypeSlug = rs.getProject().getProjectType().getSlug();
        this.projectSlug = rs.getProject().getSlug();
        this.downloadURL = Constants.getDiluvCDN(this.gameSlug, this.projectTypeSlug, rs.getProject().getId(), rs.getId(), rs.getName());
        this.size = rs.getSize();
        this.sha512 = rs.getSha512();
        this.downloads = rs.getDownloads();
        this.releaseType = rs.getReleaseType();
        this.classifier = rs.getClassifier();
        this.changelog = rs.getChangelog();
        this.createdAt = rs.getCreatedAt().getTime();
        this.uploaderUserId = rs.getUser().getId();
        this.uploaderUsername = rs.getUser().getUsername();
        this.uploaderDisplayName = rs.getUser().getDisplayName();
        this.gameVersions = gameVersions;
    }
}