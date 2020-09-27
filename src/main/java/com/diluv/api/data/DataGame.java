package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.FeaturedGamesEntity;
import com.diluv.confluencia.database.record.GamesEntity;
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

    @Expose
    private final List<DataProjectType> projectTypes;

    @Expose
    private final List<DataGameVersion> versions;

    @Expose
    private final List<DataSort> sort;

    @Expose
    private final Long projectCount;

    @Expose
    private final String defaultProjectType;

    public DataGame (GamesEntity rs) {

        this(rs, null, null);
    }

    public DataGame (FeaturedGamesEntity rs) {

        this(rs.getGame(), null, null);
    }

    public DataGame (GamesEntity rs,
                     List<DataSort> sort,
                     Long projectCount) {

        super(rs);
        this.url = rs.getUrl();
        this.defaultProjectType = rs.getDefaultProjectTypeSlug();
        this.projectTypes = rs.getProjectTypes().stream().map(DataProjectType::new).collect(Collectors.toList());
        this.versions = rs.getGameVersions().stream().map(DataGameVersion::new).collect(Collectors.toList());
        this.logoURL = Constants.getGameLogoURL(rs.getSlug());
        this.sort = sort;
        this.projectCount = projectCount;
    }
}