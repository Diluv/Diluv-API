package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataGameVersion;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.google.gson.annotations.Expose;

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
     * The byte size of the file.
     */
    @Expose
    private final long size;

    /**
     * The SHA-512 hash of the file.
     */
    @Expose
    private final String sha512;

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

    public DataSiteProjectFileDisplay(ProjectFileRecord rs, List<DataGameVersion> gameVersions, String gameSlug, String projectTypeSlug, String projectSlug) {

        this.id = rs.getId();
        this.name = rs.getName();
        this.size = rs.getSize();
        this.sha512 = rs.getSha512();
        this.releaseType = rs.getReleaseType();
        this.classifier = rs.getClassifier();
        this.createdAt = rs.getCreatedAt();
        this.uploaderUserId = rs.getUserId();
        this.uploaderUsername = rs.getUsername();
        this.gameVersions = gameVersions;
        this.gameSlug = gameSlug;
        this.projectTypeSlug = projectTypeSlug;
        this.projectSlug = projectSlug;
    }
}