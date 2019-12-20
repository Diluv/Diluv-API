package com.diluv.api.endpoints.v1.user;

import java.util.List;
import java.util.stream.Collectors;

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
import com.diluv.api.utils.error.ErrorResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

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
            // TODO Error, this probably shouldn't ever error, but check it in case
            return null;
        }

        String username = usernameParam;
        boolean authorized = false;
        String token = RequestUtil.getToken(exchange);

        if (token != null) {
            String tokenUsername = RequestUtil.getUsernameFromToken(token);
            if (tokenUsername == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
            }
            if ("me".equalsIgnoreCase(usernameParam) || tokenUsername.equalsIgnoreCase(usernameParam)) {
                username = tokenUsername;
                authorized = true;
            }
        }
        else if ("me".equalsIgnoreCase(usernameParam)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
        }

        // Todo fetch user from me/username, if logged in fetch additional data
        UserRecord userRecord = this.userDAO.findOneByUsername(username);
        if (userRecord == null) {
            // TODO Error, Database select error or a connection error, this should be logged as it could show a larger problem
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
            // TODO Error, same as above. And move to this code to a central location
            return null;
        }
        String username = usernameParam;
        boolean authorized = false;
        String token = RequestUtil.getToken(exchange);
        if (token != null) {
            String tokenUsername = RequestUtil.getUsernameFromToken(token);
            if (tokenUsername == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
            }

            if ("me".equalsIgnoreCase(usernameParam) || tokenUsername.equalsIgnoreCase(usernameParam)) {
                username = tokenUsername;
                authorized = true;
            }
        }
        else if ("me".equalsIgnoreCase(usernameParam)) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
        }

        Long userId = this.userDAO.findUserIdByUsername(username);
        if (userId == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_USER);
        }
        //TODO Maybe switch to a singular SQL statement
        List<ProjectRecord> projectRecords = this.projectDAO.findAllByUserId(userId);
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
