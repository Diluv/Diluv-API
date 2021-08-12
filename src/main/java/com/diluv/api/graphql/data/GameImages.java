package com.diluv.api.graphql.data;

import com.diluv.api.data.DataImage;

public class GameImages {

    private final Image logoURL;
    private final Image backgroundURL;
    private final Image foregroundURL;

    public GameImages (DataImage logoURL, DataImage backgroundURL, DataImage foregroundURL) {

        this.logoURL = new Image(logoURL);
        this.backgroundURL = new Image(backgroundURL);
        this.foregroundURL = new Image(foregroundURL);

    }

}
