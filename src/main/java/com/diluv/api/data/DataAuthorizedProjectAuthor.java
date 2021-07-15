package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.database.record.ProjectAuthorPermissionsEntity;
import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a project author from the perspective of an authorized user.
 */
public class DataAuthorizedProjectAuthor extends DataProjectAuthor {

    /**
     * The permissions the auth has.
     */
    @Expose
    private final List<String> permissions;

    public DataAuthorizedProjectAuthor (ProjectAuthorsEntity projectAuthor) {

        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions().stream().map(ProjectAuthorPermissionsEntity::getPermission).collect(Collectors.toList());
    }

    public DataAuthorizedProjectAuthor (UsersEntity projectAuthor, String role, List<String> permissions) {

        super(projectAuthor, role);
        this.permissions = permissions;
    }
}