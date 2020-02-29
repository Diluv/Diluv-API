package com.diluv.api.utils.permissions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.diluv.api.utils.auth.tokens.LongLivedAccessToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectRecord;

import static com.diluv.api.Main.DATABASE;

public enum ProjectPermissions {

    PROJECT_EDIT("project.edit"),
    FILE_EDIT("file.edit"),
    FILE_UPLOAD("file.upload"),
    ;

    private final String name;

    ProjectPermissions (String name) {

        this.name = name;
    }

    public String getName () {

        return this.name;
    }

    public static boolean hasPermission (ProjectRecord projectRecord, Token token, ProjectPermissions permissions) {

        List<String> userPermissions;
        if (token instanceof LongLivedAccessToken) {
            userPermissions = ((LongLivedAccessToken) token).getPermissions();
        }
        else {
            userPermissions = getAuthorizedUserPermissions(projectRecord, token);
        }
        return userPermissions != null && userPermissions.contains(permissions.getName());
    }

    protected static List<String> getAuthorizedUserPermissions (ProjectRecord projectRecord, Token token) {

        if (token instanceof LongLivedAccessToken) {
            return ((LongLivedAccessToken) token).getPermissions();
        }

        if (token.getUserId() == projectRecord.getUserId()) {
            return ProjectPermissions.getAllPermissions();
        }
        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == token.getUserId()).findFirst();

        return record.map(ProjectAuthorRecord::getPermissions).orElse(null);
    }

    public static List<String> getAuthorizedUserPermissions (ProjectRecord projectRecord, Token token, List<ProjectAuthorRecord> records) {

        if (token instanceof LongLivedAccessToken) {
            return ((LongLivedAccessToken) token).getPermissions();
        }

        if (token.getUserId() == projectRecord.getUserId()) {
            return ProjectPermissions.getAllPermissions();
        }

        final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == token.getUserId()).findFirst();

        return record.map(ProjectAuthorRecord::getPermissions).orElse(null);
    }

    public static List<String> getAllPermissions () {

        return Arrays.stream(ProjectPermissions.values()).map(ProjectPermissions::getName).collect(Collectors.toList());
    }
}
