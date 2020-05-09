package com.diluv.api.utils.auth;

import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.GameVersionRecord;
import com.diluv.confluencia.database.record.ProjectRecord;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.testcontainers.shaded.com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.diluv.api.Main.DATABASE;

public class Validator {

    public static boolean validateEmail (String email) {

        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validateUsername (String username) {

        if (GenericValidator.isBlankOrNull(username) || username.length() > 50 || username.length() < 3) {
            return false;
        }
        return GenericValidator.matchRegexp(username, "([A-Za-z0-9-_]+)");
    }

    public static boolean validatePassword (String password) {

        return !GenericValidator.isBlankOrNull(password) && password.length() <= 70 && password.length() >= 8;
    }

    public static boolean validateProjectName (String projectName) {

        if (GenericValidator.isBlankOrNull(projectName) || projectName.length() > 50 || projectName.length() < 5) {
            return false;
        }
        return GenericValidator.matchRegexp(projectName, "([A-Za-z0-9-_:]+)");
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

    private static final Set<String> VALID_RELEASE_TYPES = Collections.unmodifiableSet(Sets.newHashSet("release", "beta", "alpha"));
    private static final Set<String> VALID_CLASSIFIERS = Collections.unmodifiableSet(Sets.newHashSet("binary"));
    
    public static boolean validateReleaseType (String releaseType) {

        return releaseType != null && VALID_RELEASE_TYPES.contains(releaseType.toLowerCase());
    }

    public static boolean validateClassifier (String classifier) {

        return classifier != null && VALID_CLASSIFIERS.contains(classifier.toLowerCase());
    }

    public static List<GameVersionRecord> validateGameVersions (String gameSlug, String formGameVersions) throws MismatchException {

        List<GameVersionRecord> gameVersionRecords = new ArrayList<>();
        if (formGameVersions != null) {
            String[] gameVersions = formGameVersions.split(",");
            if (gameVersions.length > 0) {
                //TODO make game versions unique
                gameVersionRecords = DATABASE.gameDAO.findGameVersionsByGameSlugAndVersions(gameSlug, gameVersions);
                if (gameVersionRecords.size() != gameVersions.length) {
                    List<String> versionNotFound = new ArrayList<>(Arrays.asList(gameVersions));
                    for (GameVersionRecord record : gameVersionRecords) {
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

    public static List<ProjectRecord> validateDependencies (long projectId, String formDependencies) throws NumberFormatException, MismatchException {

        List<ProjectRecord> projectRecords = new ArrayList<>();
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

                projectRecords = DATABASE.projectDAO.findAllProjectsByProjectIds(dependencies);
                if (projectRecords.size() != dependencies.length) {
                    List<Long> projectNotFound = Arrays.stream(dependencies).boxed().collect(Collectors.toList());
                    for (ProjectRecord record : projectRecords) {
                        for (long id : dependencies) {
                            if (record.getId() == id) {
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
        return projectRecords;
    }
}
