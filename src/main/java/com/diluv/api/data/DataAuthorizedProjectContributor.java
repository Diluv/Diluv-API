package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.database.record.ProjectAuthorPermissionsEntity;
import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.google.gson.annotations.Expose;

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
}