package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.graphql.data.GameVersion;
import com.diluv.api.graphql.data.ProjectFile;
import graphql.kickstart.tools.GraphQLResolver;

public class ProjectFileResolver implements GraphQLResolver<ProjectFile> {

    public List<GameVersion> gameVersions (ProjectFile game) {

        return game.getEntity().getGameVersions().stream().map(e -> new GameVersion(e.getGameVersion())).collect(Collectors.toList());
    }
}
