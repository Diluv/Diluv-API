package com.diluv.api.utils.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.v1.games.FileDependency;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.TagsEntity;

public class Validator {

    public static final List<String> DEP_TYPES = Arrays.asList("required", "optional", "incompatible");
    public static final Set<String> VALID_RELEASE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("release", "beta", "alpha")));
    public static final Set<String> VALID_CLASSIFIERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("binary")));

    public static boolean validateReleaseType (String releaseType) {

        return VALID_RELEASE_TYPES.contains(releaseType.toLowerCase());
    }

    public static boolean validateClassifier (String classifier) {

        return VALID_CLASSIFIERS.contains(classifier.toLowerCase());
    }

    public static List<GameVersionsEntity> validateGameVersions (GamesEntity game, Set<String> gameVersions) throws MismatchException {

        final List<GameVersionsEntity> gameVersionRecords = new ArrayList<>();
        if (gameVersions != null && !gameVersions.isEmpty()) {
            List<String> versionNotFound = new ArrayList<>(gameVersions);
            for (GameVersionsEntity record : game.getGameVersions()) {
                for (String gameVersion : gameVersions) {
                    if (record.getVersion().equalsIgnoreCase(gameVersion)) {
                        versionNotFound.remove(gameVersion);
                        gameVersionRecords.add(record);
                    }
                }
            }
            if (!versionNotFound.isEmpty()) {
                throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_GAME_VERSION, String.join(", ", versionNotFound));
            }
        }
        return gameVersionRecords;
    }

    public static List<ProjectFileDependenciesEntity> validateDependencies (Session session, long projectId, List<FileDependency> dependencies) throws NumberFormatException, MismatchException {

        if (dependencies != null && !dependencies.isEmpty()) {
            Set<Long> projectIds = new HashSet<>();
            for (FileDependency dependency : dependencies) {
                if (dependency.projectId == null) {
                    throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID, "Dependency projectId can't be null");
                }
                if (projectId == dependency.projectId) {
                    throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_DEPEND_SELF, null);
                }
                if (!DEP_TYPES.contains(dependency.type.toLowerCase())) {
                    throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_TYPE, null);
                }
                projectIds.add(dependency.projectId);
            }

            List<Long> projects = Confluencia.PROJECT.findAllProjectsByProjectIds(session, projectIds);
            if (projects.size() != dependencies.size()) {
                projectIds.removeAll(projects);
                String missing = projectIds.stream().map(Object::toString).collect(Collectors.joining(", "));
                throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID, "Project ID not found: " + missing);
            }

            List<ProjectFileDependenciesEntity> projectFile = new ArrayList<>();
            for (FileDependency dep : dependencies) {
                ProjectFileDependenciesEntity pf = new ProjectFileDependenciesEntity();
                pf.setDependencyProject(new ProjectsEntity(dep.projectId));
                pf.setType(dep.type.toLowerCase());
                projectFile.add(pf);
            }
            return projectFile;
        }


        return Collections.emptyList();
    }

    public static List<TagsEntity> validateTags (ProjectTypesEntity projectType, List<String> tags) {

        if (!tags.isEmpty()) {
            List<TagsEntity> projectTypeTags = projectType.getTags();

            List<TagsEntity> tagRecords = new ArrayList<>();
            for (TagsEntity record : projectTypeTags) {
                if (tags.contains(record.getSlug())) {
                    tagRecords.add(record);
                }
            }

            return tagRecords;
        }
        return Collections.emptyList();
    }

    public static boolean validateUserDisplayName (String username, String displayName) {

        return username.equalsIgnoreCase(displayName);
    }

    public static boolean validateMFA (Integer mfa) {

        return mfa != null && mfa >= 0 && mfa <= 99999999;
    }


    public static List<ProjectTypeLoadersEntity> validateProjectTypeLoaders (ProjectTypesEntity projectType, Set<String> loaders) throws MismatchException {

        final List<ProjectTypeLoadersEntity> loadersRecords = new ArrayList<>();
        if (loaders != null && !loaders.isEmpty()) {
            List<String> loadersNotFound = new ArrayList<>(loaders);
            for (ProjectTypeLoadersEntity record : projectType.getProjectTypeLoaders()) {
                for (String loader : loaders) {
                    if (record.getSlug().equalsIgnoreCase(loader)) {
                        loadersNotFound.remove(loader);
                        loadersRecords.add(record);
                    }
                }
            }
            if (!loadersNotFound.isEmpty()) {
                throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_LOADER, String.join(", ", loadersNotFound));
            }
        }
        return loadersRecords;
    }
}
