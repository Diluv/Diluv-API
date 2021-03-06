package com.diluv.api.data.site;

import com.diluv.api.data.DataImage;
import com.diluv.api.data.DataSlugName;
import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.FeaturedGamesEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.google.gson.annotations.Expose;

public class DataSiteGame extends DataSlugName {
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

    @Expose
    private final String defaultProjectType;

    public DataSiteGame (FeaturedGamesEntity rs) {

        this(rs.getGame());
    }

    public DataSiteGame (GamesEntity rs) {

        super(rs.getSlug(), rs.getName());

        this.url = rs.getUrl();
        this.logoURL = Constants.getGameLogoURL(rs.getSlug());
        this.defaultProjectType = rs.getDefaultProjectTypeSlug();
    }
}
