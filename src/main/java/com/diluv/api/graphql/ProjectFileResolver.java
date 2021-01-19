package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.graphql.data.GameVersion;
import com.diluv.api.graphql.data.ProjectFile;
import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import graphql.kickstart.tools.GraphQLResolver;

public class ProjectFileResolver implements GraphQLResolver<ProjectFile> {

    public List<GameVersion> gameVersions (ProjectFile file) {

        return file.getEntity().getGameVersions().stream().map(e -> new GameVersion(e.getGameVersion())).collect(Collectors.toList());
    }

    public String downloadURL (ProjectFile file) {

        final ProjectFilesEntity projectFilesEntity = file.getEntity();
        if (projectFilesEntity.isReleased()) {
            final ProjectsEntity projectsEntity = projectFilesEntity.getProject();
            return Constants.getFileURL(projectsEntity.getGame().getSlug(), projectsEntity.getProjectType().getSlug(), projectsEntity.getId(), projectFilesEntity.getId(), projectFilesEntity.getName());
        }
        return null;
    }
}
