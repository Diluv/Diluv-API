package com.diluv.api.endpoints.v1.user;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.endpoints.v1.game.project.DataProject;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.InvalidTokenException;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.UserRecord;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class UserAPI extends RoutingHandler {
    
    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    
    public UserAPI(UserDAO userDAO, ProjectDAO projectDAO) {
        
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.get("/{username}", this::getUserByUsername);
        this.get("/{username}/projects", this::getProjectsByUsername);
    }
    
    private IResponse getUserByUsername (HttpServerExchange exchange) throws InvalidTokenException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        
        final String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_INVALID_USERNAME);
        }
        
        boolean authorized = false;
        String username = usernameParam;
        if (token != null) {
            if ("me".equalsIgnoreCase(usernameParam) || token.getUsername().equalsIgnoreCase(usernameParam)) {
                username = token.getUsername();
                authorized = true;
            }
        }
        else if ("me".equalsIgnoreCase(usernameParam)) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_REQUIRED_TOKEN);
        }
        
        final UserRecord userRecord = this.userDAO.findOneByUsername(username);
        if (userRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_USER);
        }
        
        if (authorized) {
            return ResponseUtil.successResponse(exchange, new DataAuthorizedUser(userRecord));
        }
        return ResponseUtil.successResponse(exchange, new DataUser(userRecord));
    }
    
    private IResponse getProjectsByUsername (HttpServerExchange exchange) throws InvalidTokenException {
        
        final AccessToken token = JWTUtil.getToken(exchange);
        
        // TODO Implement a filter/pagination
        final String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_INVALID_USERNAME);
        }
        
        boolean authorized = false;
        String username = usernameParam;
        if (token != null) {
            if ("me".equalsIgnoreCase(usernameParam) || token.getUsername().equalsIgnoreCase(usernameParam)) {
                username = token.getUsername();
                authorized = true;
            }
        }
        else if ("me".equalsIgnoreCase(usernameParam)) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_REQUIRED_TOKEN);
        }
        
        List<ProjectRecord> projectRecords;
        if (authorized) {
            projectRecords = this.projectDAO.findAllByUsernameWhereAuthorized(username);
        }
        else {
            projectRecords = this.projectDAO.findAllByUsername(username);
        }
        final List<DataProject> projects = projectRecords.stream().map(DataProject::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
