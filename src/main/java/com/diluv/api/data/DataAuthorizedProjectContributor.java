package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectAuthorPermissionsEntity;
import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a project contributor from the perspective of an authorized user.
 */
public class DataAuthorizedProjectContributor extends DataProjectContributor {

    /**
     * The permissions the auth has.
     */
    @Expose
    private final List<String> permissions;

    public DataAuthorizedProjectContributor (ProjectAuthorsEntity projectAuthor) {

        super(projectAuthor);
        this.permissions = projectAuthor.getPermissions().stream().map(ProjectAuthorPermissionsEntity::getPermission).collect(Collectors.toList());
    }

    public DataAuthorizedProjectContributor (UsersEntity projectAuthor, String role, List<String> permissions) {

        super(projectAuthor, role);
        this.permissions = permissions;
    }
}