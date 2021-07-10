package com.diluv.api.utils.permissions;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.database.record.ProjectAuthorPermissionsEntity;
import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ProjectPermissions {

    PROJECT_EDIT("project.edit"),
    FILE_EDIT("file.edit"),
    FILE_DELETE("file.delete"),
    FILE_UPLOAD("file.upload"),
    ;

    private static final List<String> PERMISSION_LIST = new ArrayList<>();

    static {
        Arrays.stream(ProjectPermissions.values()).forEach(projectPermissions -> PERMISSION_LIST.add(projectPermissions.name));
    }

    private final String name;

    ProjectPermissions (String name) {

        this.name = name;
    }

    public String getName () {

        return this.name;
    }

    public static boolean hasPermission (ProjectsEntity project, @Nullable Token token, ProjectPermissions permissions) {

        if (token == null)
            return false;

        List<String> userPermissions = getAuthorizedUserPermissions(project, token);

        return userPermissions != null && userPermissions.contains(permissions.getName());
    }

    @Nullable
    public static List<String> getAuthorizedUserPermissions (ProjectsEntity project, Token token) {

        if (token.getUserId() == project.getOwner().getId()) {

            return getSubsetPermissions(getAllPermissions(), token);
        }

        final Optional<ProjectAuthorsEntity> record = project.getAuthors().stream().filter(r -> r.getUser().getId() == token.getUserId()).findAny();

        if (record.isPresent()) {
            ProjectAuthorsEntity r = record.get();
            final List<String> permissions = r.getPermissions().stream().map(ProjectAuthorPermissionsEntity::getPermission).collect(Collectors.toList());
            return getSubsetPermissions(permissions, token);
        }

        return null;
    }

    /**
     * Intersection of the two sets for project permissions.
     *
     * @param permissions The permissions the user has for the project
     * @param token The token containing the global permissions
     * @return The intersection of the permissions.
     */
    private static List<String> getSubsetPermissions (List<String> permissions, Token token) {

        permissions.retainAll(token.getGlobalProjectPermissions());
        return permissions;
    }

    public static List<String> getAllPermissions () {

        return Arrays.stream(ProjectPermissions.values()).map(ProjectPermissions::getName).collect(Collectors.toList());
    }

    public static boolean validatePermissions (List<String> permissions) {

        for (String permission : permissions) {
            if (!ProjectPermissions.PERMISSION_LIST.contains(permission)) {
                return false;
            }
        }
        return true;
    }
}
