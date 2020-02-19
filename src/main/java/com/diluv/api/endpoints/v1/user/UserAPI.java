package com.diluv.api.endpoints.v1.user;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.game.domain.ProjectDomain;
import com.diluv.api.endpoints.v1.user.domain.AuthorizedUserDomain;
import com.diluv.api.endpoints.v1.user.domain.UserDomain;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.InvalidTokenException;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.UserRecord;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class UserAPI extends RoutingHandler {

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;

    public UserAPI (UserDAO userDAO, ProjectDAO projectDAO) {

        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.get("/{username}", this::getUserByUsername);
        this.get("/{username}/projects", this::getProjectsByUsername);
    }

    private Domain getUserByUsername (HttpServerExchange exchange) throws InvalidTokenException {

        final AccessToken token = JWTUtil.getToken(exchange);

        String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }


        UserRecord userRecord = this.userDAO.findOneByUsername(username);
        if (userRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }

        if (authorized) {
            return ResponseUtil.successResponse(exchange, new AuthorizedUserDomain(userRecord));
        }
        return ResponseUtil.successResponse(exchange, new UserDomain(userRecord));
    }

    private Domain getProjectsByUsername (HttpServerExchange exchange) throws InvalidTokenException {

        final AccessToken token = JWTUtil.getToken(exchange);

        // TODO Implement a filter/pagination
        String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }

        List<ProjectRecord> projectRecords;
        if (authorized) {
            projectRecords = this.projectDAO.findAllByUsernameWhereAuthorized(username);
        }
        else {
            projectRecords = this.projectDAO.findAllByUsername(username);
        }
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
