package com.diluv.api.data;

import java.util.List;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.GameRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents the data for a game that we support.
 */
public class DataGame extends DataBaseGame {

    /**
     * A URL that links to the official home page of the game.
     */
    @Expose
    private final String url;

    /**
     * A URL that links to the image of the game.
     */
    @Expose
    private final DataImage logoURL;

    /**
     * A URL that links to the banner of the game.
     */
    @Expose
    private final DataImage bannerURL;

    @Expose
    private final List<DataProjectType> projectTypes;

    @Expose
    private final List<DataGameVersion> versions;

    @Expose
    private final List<DataSort> sort;

    @Expose
    private final Long projectCount;

    public DataGame (GameRecord rs) {

        this(rs, null, null, null, null);
    }

    public DataGame (GameRecord rs,
                     List<DataProjectType> projectTypes,
                     List<DataGameVersion> versions,
                     List<DataSort> sort,
                     Long projectCount) {

        super(rs);
        this.url = rs.getUrl();
        this.logoURL = Constants.getGameLogoURL(rs.getSlug());
        this.bannerURL = Constants.getGameBannerURL(rs.getSlug());
        this.projectTypes = projectTypes;
        this.versions = versions;
        this.sort = sort;
        this.projectCount = projectCount;
    }
}