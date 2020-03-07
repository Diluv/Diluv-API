package com.diluv.api.v1.users;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.data.DataAuthorizedUser;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataUser;
import com.diluv.api.utils.auth.tokens.AccessToken;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.UserRecord;
import com.diluv.confluencia.utils.Pagination;

import org.jboss.resteasy.annotations.cache.Cache;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/users")
public class UsersAPI {

    @GET
    @Path("/self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf (@HeaderParam("Authorization") String auth) {

        if (auth == null) {

            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        final AccessToken token = AccessToken.getToken(auth);

        if (token == null) {

            return ErrorMessage.USER_INVALID_TOKEN.respond();
        }


        final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(token.getUsername());

        if (userRecord == null) {

            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        return ResponseUtil.successResponse(new DataAuthorizedUser(userRecord));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser (@PathParam("username") String username, @HeaderParam("Authorization") String auth) {

        final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(username);

        if (userRecord == null) {

            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        if (auth != null) {

            final AccessToken token = AccessToken.getToken(auth);

            if (token != null && token.getUsername().equalsIgnoreCase(username)) {

                return ResponseUtil.successResponse(new DataAuthorizedUser(userRecord));
            }
        }

        return ResponseUtil.successResponse(new DataUser(userRecord));
    }

    @GET
    @Path("/{username}/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectsByUsername (@PathParam("username") String username, @HeaderParam("Authorization") String auth, @QueryParam("cursor") String queryCursor, @QueryParam("limit") Integer queryLimit) {

        final AccessToken token = AccessToken.getToken(auth);
        Pagination pagination = Pagination.getPagination(queryCursor);
        int limit = Pagination.getLimit(queryLimit);

        List<ProjectRecord> projectRecords;

        if (token != null && token.getUsername().equalsIgnoreCase(username)) {

            projectRecords = DATABASE.projectDAO.findAllByUsernameWhereAuthorized(username, pagination, limit + 1);
        }

        else {

            projectRecords = DATABASE.projectDAO.findAllByUsername(username, pagination, limit + 1);
        }

        final List<DataProject> projects = projectRecords.stream().limit(limit).map(DataProject::new).collect(Collectors.toList());
        return ResponseUtil.successResponsePagination(projects, projectRecords.size() > limit ? new Pagination(limit + pagination.offset).getCursor() : null);
    }
}