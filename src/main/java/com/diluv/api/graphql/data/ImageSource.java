package com.diluv.api.graphql.data;

import com.diluv.api.data.DataImageSource;

public class ImageSource {
    private String src;
    private String type;

    public ImageSource (DataImageSource dis) {

        this.src = dis.getSrc();
        this.type = dis.getType();
    }
}
