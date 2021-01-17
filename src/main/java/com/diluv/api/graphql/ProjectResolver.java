package com.diluv.api.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.graphql.data.Author;
import com.diluv.api.graphql.data.Game;
import com.diluv.api.graphql.data.Project;
import com.diluv.api.graphql.data.ProjectFile;
import com.diluv.api.graphql.data.ProjectType;
import com.diluv.api.graphql.data.SlugName;
import com.diluv.api.graphql.data.User;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTagsEntity;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import graphql.kickstart.tools.GraphQLResolver;

public class ProjectResolver implements GraphQLResolver<Project> {

    public List<Author> authors (Project project) {

        List<Author> authors = new ArrayList<>();
        authors.add(new Author(new User(project.getEntity().getOwner()), "owner"));
        authors.addAll(project.getEntity().getAuthors().stream().map(a -> new Author(new User(a.getUser()), a.getRole())).collect(Collectors.toList()));
        return authors;
    }

    public ProjectType projectType (Project project) {

        return new ProjectType(project.getEntity().getProjectType());
    }

    public Game game (Project project) {

        return new Game(project.getEntity().getGame());
    }

    public List<SlugName> tags (Project project) {

        final List<ProjectTagsEntity> tags = project.getEntity().getTags();
        return tags.stream().map(e -> new SlugName(e.getTag().getSlug(), e.getTag().getName())).collect(Collectors.toList());
    }

    public List<ProjectFile> files (Project project) {

        return Confluencia.getTransaction(session -> {
            final List<ProjectFilesEntity> projectFiles = Confluencia.FILE.findAllByProject(session, project.getEntity(), true, 1, 25, ProjectFileSort.NEW, null, "");
            return projectFiles.stream().map(ProjectFile::new).collect(Collectors.toList());
        });
    }
}
