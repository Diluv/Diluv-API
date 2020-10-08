package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.data.DataImage;
import graphql.kickstart.tools.GraphQLResolver;

public class GameResolver implements GraphQLResolver<Game> {

    public List<ProjectType> projectTypes (Game game) {

        return game.getEntity().getProjectTypes().stream().map(ProjectType::new).collect(Collectors.toList());
    }

    public List<GameVersion> gameVersions (Game game) {

        return game.getEntity().getGameVersions().stream().map(GameVersion::new).collect(Collectors.toList());
    }
}
