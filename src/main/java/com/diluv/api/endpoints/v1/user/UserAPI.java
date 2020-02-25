package com.diluv.api.endpoints.v1.user;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.aaa.RoutingHandlerPlus;
import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.endpoints.v1.game.project.DataProject;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.UserRecord;

import io.undertow.server.HttpServerExchange;

public class UserAPI extends RoutingHandlerPlus {
    
    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    
    public UserAPI(UserDAO userDAO, ProjectDAO projectDAO) {
        
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        
        this.get("/self", this::getSelf);
        this.get("/{username}", "username", this::getUserByUsername);
        this.get("/{username}/projects", "username", this::getProjectsByUsername);
    }
    
    private IResponse getSelf(HttpServerExchange exchange, AccessToken token) {
        
        final UserRecord userRecord = this.userDAO.findOneByUsername(token.getUsername());
        
        if (userRecord == null) {
            
            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_USER);
        }
        
        return ResponseUtil.successResponse(exchange, new DataAuthorizedUser(userRecord));
    }
    
    private IResponse getUserByUsername (HttpServerExchange exchange, String username) {

        final UserRecord userRecord = this.userDAO.findOneByUsername(username);

        if (userRecord == null) {
            
            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_USER);
        }
        
        final AccessToken token = JWTUtil.getTokenSafely(exchange);

        if (token != null && token.getUsername().equalsIgnoreCase(username)) {
            
            return ResponseUtil.successResponse(exchange, new DataAuthorizedUser(userRecord));
        }
        
        return ResponseUtil.successResponse(exchange, new DataUser(userRecord));
    }
    
    private IResponse getProjectsByUsername (HttpServerExchange exchange, String username) {

        final AccessToken token = JWTUtil.getTokenSafely(exchange);

        List<ProjectRecord> projectRecords;
        
        if ( token != null && token.getUsername().equalsIgnoreCase(username)) {
            
            projectRecords = this.projectDAO.findAllByUsernameWhereAuthorized(username);
        }
        
        else {
            
            projectRecords = this.projectDAO.findAllByUsername(username);
        }
        
        final List<DataProject> projects = projectRecords.stream().map(DataProject::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
