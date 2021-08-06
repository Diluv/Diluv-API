package com.diluv.api.data;

import com.diluv.api.utils.Constants;
import com.google.gson.annotations.Expose;

public class DataGameLogos {

    /**
     * A URL that links to the image of the game.
     */
    @Expose
    private final DataImage logoURL;

    @Expose
    private final DataImage backgroundURL;

    @Expose
    private final DataImage foregroundURL;

    public DataGameLogos (String slug) {

        this.logoURL = Constants.getGameLogoURL(slug);
        this.backgroundURL = Constants.getGameBackgroundURL(slug);
        this.foregroundURL = Constants.getGameForegroundURL(slug);
    }
}
