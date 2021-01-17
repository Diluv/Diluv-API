package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.graphql.data.Game;
import com.diluv.api.graphql.data.Loader;
import com.diluv.api.graphql.data.ProjectType;
import com.diluv.api.graphql.data.SlugName;
import com.diluv.confluencia.database.record.TagsEntity;
import graphql.kickstart.tools.GraphQLResolver;

public class ProjectTypeResolver implements GraphQLResolver<ProjectType> {

    public List<SlugName> tags (ProjectType projectType) {

        final List<TagsEntity> tags = projectType.getEntity().getTags();
        return tags.stream().map(e -> new SlugName(e.getSlug(), e.getName())).collect(Collectors.toList());
    }

    public List<Loader> loaders (ProjectType projectType) {

        return projectType.getEntity().getProjectTypeLoaders().stream().map(Loader::new).collect(Collectors.toList());
    }

    public Game game (ProjectType projectType) {

        return new Game(projectType.getEntity().getGame());
    }
}
