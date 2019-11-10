package com.diluv.api.endpoints.v1.user;

import java.util.List;
import java.util.stream.Collectors;

import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.undertow.account.Pac4jAccount;
import org.pac4j.undertow.handler.SecurityHandler;

import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.database.record.UserRecord;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.endpoints.v1.user.domain.UserDomain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ErrorType;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class UserAPI extends RoutingHandler {

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;

    public UserAPI (UserDAO userDAO, ProjectDAO projectDAO) {

        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.get("/v1/user/{username}", SecurityHandler.build(this::getUserByUsername, Constants.CONFIG, Constants.REQUEST_JWT_OPTIONAL));
        this.get("/v1/user/{username}/projects", SecurityHandler.build(this::getProjectsByUsername, Constants.CONFIG, Constants.REQUEST_JWT_OPTIONAL));
    }

    private Domain getUserByUsername (HttpServerExchange exchange) {

        String usernameParam = RequestUtil.getParam(exchange, "username");
        if (usernameParam == null) {
            // TODO Error, this probably shouldn't ever error, but check it in case
            return null;
        }
        boolean authorized = false;

        String username;
        if ("me".equalsIgnoreCase(usernameParam)) {
            Pac4jAccount account = RequestUtil.getAccount(exchange);
            if (account != null && !(account.getProfile() instanceof AnonymousProfile)) {
                username = account.getProfile().getUsername();
                authorized = true;
            }
            else {
                // TODO Error, means they were asking for "me", but didn't supply a valid access token
                return ResponseUtil.errorResponse(exchange, ErrorType.UNAUTHORIZED, "Invalid token");
            }
        }
        else {
            username = usernameParam;
        }
        // Todo fetch user from me/username, if logged in fetch additional data
        UserRecord userRecord = this.userDAO.findOneByUsername(username);
        if (userRecord == null) {
            // TODO Error, Database select error or a connection error, this should be logged as it could show a larger problem
            return ResponseUtil.errorResponse(exchange, ErrorType.BAD_REQUEST, "User not found");
        }

        if (authorized) {
            return null;
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
        String username;
        if ("me".equalsIgnoreCase(usernameParam)) {
            Pac4jAccount account = RequestUtil.getAccount(exchange);
            if (account != null) {
                username = account.getProfile().getUsername();
            }
            else {
                // TODO Error, means they were asking for "me", but didn't supply a valid access token
                return ResponseUtil.errorResponse(exchange, ErrorType.UNAUTHORIZED, "Invalid token");
            }
        }
        else {
            username = usernameParam;
        }

        // GET USER ID FROM USERNAME
        long userId = this.userDAO.findUserIdByUsername(username);
        List<ProjectRecord> projectRecords = this.projectDAO.findAllByUserId(userId);
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}
