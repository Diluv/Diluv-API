package com.diluv.api.utils.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;

import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.TagsEntity;

import static com.diluv.api.Main.DATABASE;

public class Validator {

    public static boolean validateEmail (String email) {

        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validateUsername (String username) {

        if (GenericValidator.isBlankOrNull(username) || username.length() > 30 || username.length() < 3) {
            return false;
        }
        return GenericValidator.matchRegexp(username, "([A-Za-z0-9-_]+)");
    }

    public static boolean validatePassword (String password) {

        return !GenericValidator.isBlankOrNull(password) && password.length() <= 70 && password.length() >= 8;
    }

    public static boolean validateProjectName (String projectName) {

        return !GenericValidator.isBlankOrNull(projectName) && projectName.length() <= 50 && projectName.length() >= 5;
    }

    public static boolean validateProjectSummary (String projectSummary) {

        return !GenericValidator.isBlankOrNull(projectSummary) && projectSummary.length() <= 250 && projectSummary.length() >= 10;
    }

    public static boolean validateProjectDescription (String projectDescription) {

        return !GenericValidator.isBlankOrNull(projectDescription) && projectDescription.length() <= 1000 && projectDescription.length() >= 50;
    }

    public static boolean validateProjectFileChangelog (String formChangelog) {

        // TODO
        return true;
    }

    private static final Set<String> VALID_RELEASE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("release", "beta", "alpha")));
    private static final Set<String> VALID_CLASSIFIERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("binary")));

    public static boolean validateReleaseType (String releaseType) {

        return releaseType != null && VALID_RELEASE_TYPES.contains(releaseType.toLowerCase());
    }

    public static boolean validateClassifier (String classifier) {

        return classifier != null && VALID_CLASSIFIERS.contains(classifier.toLowerCase());
    }

    public static List<GameVersionsEntity> validateGameVersions (GamesEntity game, String formGameVersions) throws MismatchException {

        List<GameVersionsEntity> gameVersionRecords = game.getGameVersions();
        if (formGameVersions != null) {
            String[] gameVersions = formGameVersions.split(",");
            if (gameVersions.length > 0) {
                //TODO make game versions unique
                if (gameVersionRecords.size() != gameVersions.length) {
                    List<String> versionNotFound = new ArrayList<>(Arrays.asList(gameVersions));
                    for (GameVersionsEntity record : gameVersionRecords) {
                        for (String gameVersion : gameVersions) {
                            if (record.getVersion().equalsIgnoreCase(gameVersion)) {
                                versionNotFound.remove(gameVersion);
                            }
                        }
                    }
                    if (!versionNotFound.isEmpty()) {
                        //String.join(", ", versionNotFound)
                        throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_GAME_VERSION);
                    }
                }
            }
        }
        return gameVersionRecords;
    }

    public static List<ProjectsEntity> validateDependencies (long projectId, String formDependencies) throws NumberFormatException, MismatchException {

        List<ProjectsEntity> projects = new ArrayList<>();
        if (formDependencies != null) {
            String[] dependenciesString = formDependencies.split(",");
            if (dependenciesString.length > 0) {

                long[] dependencies = new long[dependenciesString.length];
                for (int i = 0; i < dependencies.length; i++) {
                    dependencies[i] = Long.parseLong(dependenciesString[i]);

                    if (projectId == dependencies[i]) {
                        throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_SAME_ID);
                    }
                }
                //TODO make dependency unique

                projects = DATABASE.projectDAO.findAllProjectsByProjectIds(dependencies);
                if (projects.size() != dependencies.length) {
                    List<Long> projectNotFound = Arrays.stream(dependencies).boxed().collect(Collectors.toList());
                    for (ProjectsEntity project : projects) {
                        for (long id : dependencies) {
                            if (project.getId() == id) {
                                projectNotFound.remove(id);
                            }
                        }
                    }
                    if (!projectNotFound.isEmpty()) {
                        //projectNotFound.stream().map(Object::toString).collect(Collectors.joining(", "))
                        throw new MismatchException(ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID);
                    }
                }
            }
        }
        return projects;
    }

    public static List<TagsEntity> validateTags (ProjectTypesEntity projectType, String[] tags) {

        if (tags.length > 0) {
            List<TagsEntity> projectTypeTags = projectType.getTags();
            List<String> tagList = Arrays.asList(tags);

            List<TagsEntity> tagRecords = new ArrayList<>();
            for (TagsEntity record : projectTypeTags) {
                if (tagList.contains(record.getSlug())) {
                    tagRecords.add(record);
                }
            }

            return tagRecords;
        }
        return Collections.emptyList();
    }
}
