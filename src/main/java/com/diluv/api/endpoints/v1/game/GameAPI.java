package com.diluv.api.endpoints.v1.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.game.domain.AuthorizedProjectAuthorDomain;
import com.diluv.api.endpoints.v1.game.domain.AuthorizedProjectDomain;
import com.diluv.api.endpoints.v1.game.domain.BaseProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.GameDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectAuthorDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileQueueDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.FormUtil;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.InvalidTokenException;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.github.slugify.Slugify;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

public class GameAPI extends RoutingHandler {
    
    private final Slugify slugify = new Slugify();
    private final GameDAO gameDAO;
    private final ProjectDAO projectDAO;
    private final FileDAO fileDAO;
    
    public GameAPI(GameDAO gameDAO, ProjectDAO projectDAO, FileDAO fileDAO) {
        
        this.gameDAO = gameDAO;
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.get("/", this::getGames);
        this.get("/{game_slug}", this::getGameBySlug);
        this.get("/{game_slug}/types", this::getProjectTypesByGameSlug);
        this.get("/{game_slug}/{project_type_slug}", this::getProjectTypesByGameSlugAndProjectType);
        this.get("/{game_slug}/{project_type_slug}/projects", this::getProjectsByGameSlugAndProjectType);
        this.get("/{game_slug}/{project_type_slug}/{project_slug}", this::getProjectByGameSlugAndProjectTypeAndProjectSlug);
        this.get("/{game_slug}/{project_type_slug}/{project_slug}/files", this::getProjectFilesByGameSlugAndProjectTypeAndProjectSlug);
        
        this.post("/{game_slug}/{project_type_slug}", this::postProjectByGameSlugAndProjectType);
        this.post("/{game_slug}/{project_type_slug}/{project_slug}/files", this::postProjectFilesByGameSlugAndProjectTypeAndProjectSlug);
    }
    
    private Domain getGames (HttpServerExchange exchange) {
        
        final List<GameRecord> gameRecords = this.gameDAO.findAll();
        final List<GameDomain> games = gameRecords.stream().map(GameDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }
    
    private Domain getGameBySlug (HttpServerExchange exchange) {
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        final GameRecord gameRecord = this.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        return ResponseUtil.successResponse(exchange, new GameDomain(gameRecord));
    }
    
    private Domain getProjectTypesByGameSlug (HttpServerExchange exchange) {
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        final List<ProjectTypeRecord> projectTypesRecords = this.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        final List<ProjectTypeDomain> projectTypes = projectTypesRecords.stream().map(ProjectTypeDomain::new).collect(Collectors.toList());
        
        return ResponseUtil.successResponse(exchange, projectTypes);
    }
    
    private Domain getProjectTypesByGameSlugAndProjectType (HttpServerExchange exchange) {
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        final ProjectTypeRecord projectTypesRecords = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
        
        if (projectTypesRecords == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        return ResponseUtil.successResponse(exchange, new ProjectTypeDomain(projectTypesRecords));
    }
    
    private Domain getProjectsByGameSlugAndProjectType (HttpServerExchange exchange) {
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        final List<ProjectRecord> projectRecords = this.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug);
        final List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
    
    private Domain getProjectByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) throws InvalidTokenException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        final String projectSlug = RequestUtil.getParam(exchange, "project_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
        }
        
        final List<ProjectAuthorRecord> records = this.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());
        
        if (!projectRecord.isReleased() && token != null) {
            final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == token.getUserId()).findFirst();
            
            if (record.isPresent()) {
                final List<ProjectAuthorDomain> projectAuthors = records.stream().map(AuthorizedProjectAuthorDomain::new).collect(Collectors.toList());
                return ResponseUtil.successResponse(exchange, new AuthorizedProjectDomain(projectRecord, projectAuthors, record.get().getPermissions()));
            }
        }
        
        final List<ProjectAuthorDomain> projectAuthors = records.stream().map(ProjectAuthorDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, new ProjectDomain(projectRecord, projectAuthors));
    }
    
    private Domain getProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) throws InvalidTokenException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        final String projectSlug = RequestUtil.getParam(exchange, "project_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
        }
        
        List<ProjectFileRecord> projectRecords;
        
        // TODO Check permissions
        if (token != null && projectRecord.getUserId() == token.getUserId()) {
            projectRecords = this.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlugAuthorized(gameSlug, projectTypeSlug, projectSlug);
        }
        else {
            projectRecords = this.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        }
        final List<BaseProjectFileDomain> projects = new ArrayList<>();
        for (final ProjectFileRecord record : projectRecords) {
            if (record.getSha512() == null) {
                projects.add(new ProjectFileQueueDomain(record, gameSlug, projectTypeSlug, projectSlug));
            }
            else {
                projects.add(new ProjectFileDomain(record, gameSlug, projectTypeSlug, projectSlug));
            }
        }
        return ResponseUtil.successResponse(exchange, projects);
    }
    
    private Domain postProjectByGameSlugAndProjectType (HttpServerExchange exchange) throws InvalidTokenException, MultiPartParserDefinition.FileTooLargeException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        if (token == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        final FormData data = FormUtil.getMultiPartForm(exchange, 1000000L);
        // Defaults to 1MB should be database stored. TODO
        
        if (data == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
        final String formName = RequestUtil.getFormParam(data, "name");
        final String formSummary = RequestUtil.getFormParam(data, "summary");
        final String formDescription = RequestUtil.getFormParam(data, "description");
        final FormData.FileItem formLogo = RequestUtil.getFormFile(data, "logo");
        
        if (!Validator.validateProjectName(formName)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_NAME);
        }
        
        if (!Validator.validateProjectSummary(formSummary)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SUMMARY);
        }
        
        if (!Validator.validateProjectDescription(formDescription)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_DESCRIPTION);
        }
        
        if (formLogo == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_LOGO);
        }
        
        FileUtil.getSize(formLogo);
        
        final BufferedImage image = ImageUtil.isValidImage(formLogo);
        if (image == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_LOGO);
        }
        final String projectSlug = this.slugify.slugify(formName);
        
        if (this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) != null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TAKEN_SLUG);
        }
        if (!this.projectDAO.insertProject(projectSlug, formName, formSummary, formDescription, token.getUserId(), gameSlug, projectTypeSlug)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_PROJECT);
        }
        
        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        
        if (projectRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
        }
        
        final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%s/logo.png", gameSlug, projectTypeSlug, projectSlug));
        if (!ImageUtil.saveImage(image, file)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_SAVING_IMAGE);
        }
        return ResponseUtil.successResponse(exchange, new ProjectDomain(projectRecord));
    }
    
    private Domain postProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) throws InvalidTokenException, MultiPartParserDefinition.FileTooLargeException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        if (token == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }
        
        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        final String projectSlug = RequestUtil.getParam(exchange, "project_slug");
        
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }
        
        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }
        
        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        
        final ProjectTypeRecord projectTypeRecord = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
        if (projectTypeRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }
        
        // TODO Needs to be moved to check the user permissions in the future
        if (projectRecord.getUserId() != token.getUserId()) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_NOT_AUTHORIZED);
        }
        final FormData data = FormUtil.getMultiPartForm(exchange, projectTypeRecord.getMaxSize());
        
        if (data == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
        
        final String formChangelog = RequestUtil.getFormParam(data, "changelog");
        final FormData.FileItem formFile = RequestUtil.getFormFile(data, "file");
        
        if (!Validator.validateProjectFileChangelog(formChangelog)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_FILE_INVALID_CHANGELOG);
        }
        
        if (formFile == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_FILE_INVALID_FILE);
        }
        
        final Long fileSize = FileUtil.getSize(formFile);
        
        final String sha512 = FileUtil.getSHA512(formFile);
        final String fileName = formFile.getFile().getFileName().toString();
        
        if (sha512 == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_SHA512);
        }
        
        final Long id = this.fileDAO.insertProjectFile(fileName, fileSize, formChangelog, sha512, projectRecord.getId(), token.getUserId());
        if (id == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_PROJECT_FILE);
        }
        
        final File file = new File(Constants.PROCESSING_FOLDER, String.format("%s/%s/%s/%s/%s", gameSlug, projectTypeSlug, projectSlug, id, fileName));
        try {
            
            FileUtils.copyInputStreamToFile(formFile.getInputStream(), file);
        }
        catch (final IOException e) {
            DiluvAPI.LOGGER.error("Failed to postProjectFilesByGameSlugAndProjectTypeAndProjectSlug.", e);
            return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_WRITING);
        }
        
        final ProjectFileRecord record = this.fileDAO.findOneProjectFileQueueByFileId(id);
        return ResponseUtil.successResponse(exchange, new ProjectFileQueueDomain(record, gameSlug, projectTypeSlug, projectSlug));
    }
}