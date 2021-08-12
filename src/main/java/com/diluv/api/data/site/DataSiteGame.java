package com.diluv.api.data.site;

import com.diluv.api.data.DataGameLogos;
import com.diluv.api.data.DataSlugName;
import com.diluv.confluencia.database.record.FeaturedGamesEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.google.gson.annotations.Expose;

public class DataSiteGame extends DataSlugName {
    /**
     * A URL that links to the official home page of the game.
     */
    @Expose
    private final String url;

    @Expose
    private final DataGameLogos logo;

    @Expose
    private final String defaultProjectType;

    public DataSiteGame (FeaturedGamesEntity rs) {

        this(rs.getGame());
    }

    public DataSiteGame (GamesEntity rs) {

        super(rs.getSlug(), rs.getName());

        this.url = rs.getUrl();
        this.logo = new DataGameLogos(rs.getSlug());
        this.defaultProjectType = rs.getDefaultProjectTypeSlug();
    }
}
