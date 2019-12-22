package com.diluv.api.endpoints.v1.user;

import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.user.domain.AuthorizedUserDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.endpoints.v1.user.domain.UserDomain;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

import java.util.List;
import java.util.stream.Collectors;

public class UserAPI extends RoutingHandler {

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;

    public UserAPI (UserDAO userDAO, ProjectDAO projectDAO) {

        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.get("/v1/users/{username}", this::getUserByUsername);
        this.get("/v1/users/{username}/projects", this::getProjectsByUsername);
    }

    private Domain getUserByUsername (HttpServerExchange exchange) {

        String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
        }

        String username = usernameParam;
        boolean authorized = false;
        String token = JWTUtil.getToken(exchange);

        if (token != null) {
            String tokenUsername = JWTUtil.getUsernameFromToken(token);
            if (tokenUsername == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_TOKEN);
            }
            if ("me".equalsIgnoreCase(usernameParam) || tokenUsername.equalsIgnoreCase(usernameParam)) {
                username = tokenUsername;
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

    private Domain getProjectsByUsername (HttpServerExchange exchange) {

        // TODO Implement a filter/pagination
        String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_USERNAME);
        }
        String username = usernameParam;
        boolean authorized = false;
        String token = JWTUtil.getToken(exchange);
        if (token != null) {
            String tokenUsername = JWTUtil.getUsernameFromToken(token);
            if (tokenUsername == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_TOKEN);
            }

            if ("me".equalsIgnoreCase(usernameParam) || tokenUsername.equalsIgnoreCase(usernameParam)) {
                username = tokenUsername;
                authorized = true;
            }
        }
        else if ("me".equalsIgnoreCase(usernameParam)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }

        Long userId = this.userDAO.findUserIdByUsername(username);
        if (userId == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }

        List<ProjectRecord> projectRecords = this.projectDAO.findAllByUserId(userId);
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
