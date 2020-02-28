package com.diluv.api.utils.permissions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static boolean hasPermission (ProjectRecord projectRecord, long userId, ProjectPermissions permissions) {

        List<String> userPermissions = getPermissions(projectRecord, userId);
        return userPermissions != null && userPermissions.contains(permissions.getName());
    }

    public static List<String> getPermissions (ProjectRecord projectRecord, long userId, List<ProjectAuthorRecord> records) {

        if (userId == projectRecord.getUserId()) {
            return ProjectPermissions.getAllPermissions();
        }
        else {
            final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == userId).findFirst();

            if (record.isPresent()) {
                return record.get().getPermissions();
            }
        }
        return null;
    }

    public static List<String> getPermissions (ProjectRecord projectRecord, long userId) {

        if (userId == projectRecord.getUserId()) {
            return ProjectPermissions.getAllPermissions();
        }
        else {
            final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

            final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == userId).findFirst();

            if (record.isPresent()) {
                return record.get().getPermissions();
            }
        }
        return Collections.emptyList();
    }

    public static List<String> getAllPermissions () {

        return Arrays.stream(ProjectPermissions.values()).map(ProjectPermissions::getName).collect(Collectors.toList());
    }
}
