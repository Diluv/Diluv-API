package com.diluv.api.v1.games.feed;

import com.diluv.api.data.DataCategory;
import com.diluv.api.data.DataProjectContributor;
import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.google.gson.annotations.Expose;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedProject {
    @Expose
    private long id;

    /**
     * The display name of the project.
     */
    @Expose
    private String name;

    /**
     * A unique slug used to identify the project in URLs and API requests.
     */
    @Expose
    private String slug;

    /**
     * A short summary of the project.
     */
    @Expose
    private String summary;

    /**
     * The logo url of the project.
     */
    @Expose
    private String logo;

    /**
     * The amount of downloads the project has.
     */
    @Expose
    private long downloads;

    /**
     * The creation data of the project.
     */
    @Expose
    private long createdAt;

    /**
     * The date when the project was last updated.
     */
    @Expose
    private long updatedAt;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private List<DataCategory> categories;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private List<DataProjectContributor> contributors = new ArrayList<>();

    public FeedProject () {
        
    }

    public FeedProject (ProjectRecord projectRecord, List<DataCategory> categories) {

        this(projectRecord, categories, null);
    }

    public FeedProject (ProjectRecord projectRecord, List<DataCategory> categories, List<DataProjectContributor> projectAuthorRecords) {

        this.id = projectRecord.getId();
        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.logo = Constants.getLogo(projectRecord.getGameSlug(), projectRecord.getProjectTypeSlug(), projectRecord.getId());
        this.downloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
        this.categories = categories;
        this.contributors.add(new DataProjectContributor(projectRecord.getUserId(), projectRecord.getUsername(), projectRecord.getUserDisplayName(), projectRecord.getUserCreatedAt(), "owner"));
        if (projectAuthorRecords != null) {
            this.contributors.addAll(projectAuthorRecords);
        }
    }
}
