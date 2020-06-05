package com.diluv.api.data.site;

import com.diluv.api.data.DataBaseGame;
import com.diluv.api.data.DataImage;
import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.GameRecord;
import com.google.gson.annotations.Expose;

public class DataSiteGame extends DataBaseGame {
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

    public DataSiteGame (GameRecord rs, String defaultProjectType) {

        super(rs);

        this.url = rs.getUrl();
        this.logoURL = Constants.getGameLogoURL(rs.getSlug());
        this.defaultProjectType = defaultProjectType;
    }
}
