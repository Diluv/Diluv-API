package com.diluv.api.graphql;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.data.DataImage;

public class Image {

    private ImageSource fallback;
    private List<ImageSource> sources;

    public Image (DataImage di) {

        this.fallback = new ImageSource(di.getFallback());
        this.sources = Arrays.stream(di.getSources()).map(ImageSource::new).collect(Collectors.toList());
    }
}
