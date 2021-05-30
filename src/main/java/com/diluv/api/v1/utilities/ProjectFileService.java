package com.diluv.api.v1.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;

import com.diluv.api.data.DataProjectFileInQueue;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.ProjectFileUploadForm;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileLoadersEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;

public class ProjectFileService {

    public static Response postProjectFile (Session session, ProjectsEntity project, Token token, ProjectFileUploadForm form) {

        if (!ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD)) {

            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        if (!Validator.validateReleaseType(form.data.releaseType)) {

            return ErrorMessage.PROJECT_FILE_INVALID_RELEASE_TYPE.respond();
        }

        if (!Validator.validateClassifier(form.data.classifier)) {

            return ErrorMessage.PROJECT_FILE_INVALID_CLASSIFIER.respond();
        }

        if (Confluencia.FILE.existsByProjectIdAndVersion(session, project.getId(), form.data.version)) {

            return ErrorMessage.PROJECT_FILE_TAKEN_VERSION.respond();
        }

        List<GameVersionsEntity> gameVersionRecords;
        List<ProjectTypeLoadersEntity> projectTypeLoaders;
        List<ProjectFileDependenciesEntity> dependencyRecords;

        try {
            gameVersionRecords = Validator.validateGameVersions(project.getGame(), form.data.gameVersions);
            projectTypeLoaders = Validator.validateProjectTypeLoaders(project.getProjectType(), form.data.loaders);
            dependencyRecords = Validator.validateDependencies(session, project.getId(), form.data.dependencies);
        }
        catch (MismatchException e) {
            if (e.getMessage() == null) {
                return e.getErrorMessage().respond();
            }
            return e.getErrorMessage().respond(e.getMessage());
        }
        catch (NumberFormatException e) {
            return ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID.respond();
        }

        final String fileName = FilenameUtils.getName(form.fileName);
        if (GenericValidator.isBlankOrNull(fileName) || !(form.fileName.equals(fileName))) {
            return ErrorMessage.PROJECT_FILE_INVALID_FILENAME.respond();
        }
        final File tempFile = FileUtil.getTempFile(project.getId(), fileName);
        final String sha512 = FileUtil.writeFile(form.file, project.getProjectType().getMaxFileSize(), tempFile);

        if (tempFile == null) {
            System.out.println("FAILED_TEMP_FILE");
            // return ErrorMessage.FAILED_TEMP_FILE.respond();
            return ErrorMessage.THROWABLE.respond();
        }

        if (sha512 == null) {
            System.out.println("FAILED_SHA512");
            //return ErrorMessage.FAILED_SHA512.respond();
            return ErrorMessage.THROWABLE.respond();
        }

        ProjectFilesEntity projectFile = new ProjectFilesEntity();
        projectFile.setName(fileName);
        projectFile.setVersion(form.data.version);
        projectFile.setSize(tempFile.length());
        projectFile.setChangelog(form.data.changelog);
        projectFile.setSha512(sha512);
        projectFile.setReleaseType(form.data.releaseType);
        projectFile.setClassifier(form.data.classifier);
        projectFile.setProject(project);
        projectFile.setUser(new UsersEntity(token.getUserId()));

        if (!gameVersionRecords.isEmpty()) {
            List<ProjectFileGameVersionsEntity> gameVersions = new ArrayList<>();
            for (GameVersionsEntity version : gameVersionRecords) {
                ProjectFileGameVersionsEntity gameVersionsEntity = new ProjectFileGameVersionsEntity();
                gameVersionsEntity.setProjectFile(projectFile);
                gameVersionsEntity.setGameVersion(version);
                gameVersions.add(gameVersionsEntity);
            }
            projectFile.setGameVersions(gameVersions);
        }

        if (!projectTypeLoaders.isEmpty()) {
            List<ProjectFileLoadersEntity> loaders = new ArrayList<>();
            for (ProjectTypeLoadersEntity loader : projectTypeLoaders) {
                ProjectFileLoadersEntity fileLoadersEntity = new ProjectFileLoadersEntity();
                fileLoadersEntity.setProjectFile(projectFile);
                fileLoadersEntity.setLoader(loader);
                loaders.add(fileLoadersEntity);
            }
            projectFile.setLoaders(loaders);
        }

        if (!dependencyRecords.isEmpty()) {
            List<ProjectFileDependenciesEntity> dependencies = new ArrayList<>();
            for (ProjectFileDependenciesEntity dependency : dependencyRecords) {
                dependency.setProjectFile(projectFile);
                dependencies.add(dependency);
            }
            projectFile.setDependencies(dependencies);
        }

        session.save(projectFile);
        session.flush();
        session.refresh(projectFile);

        final String gameSlug = project.getGame().getSlug();
        final String projectTypeSlug = project.getProjectType().getSlug();

        File destination = FileUtil.getOutputLocation(gameSlug, projectTypeSlug, project.getId(), projectFile.getId(), fileName);
        destination.getParentFile().mkdirs();
        try {
            FileUtils.copyFile(tempFile, destination);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR_WRITING");
            return ErrorMessage.THROWABLE.respond();
        } finally {
            tempFile.delete();
            tempFile.getParentFile().delete();
        }

        return ResponseUtil.successResponse(new DataProjectFileInQueue(projectFile, gameSlug, projectTypeSlug, project.getSlug()));
    }
}
