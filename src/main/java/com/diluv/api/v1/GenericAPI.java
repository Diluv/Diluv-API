package com.diluv.api.v1;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.diluv.api.utils.AuthUtilities;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.ProjectFileDownloadsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.utils.response.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;

@GZIP
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GenericAPI {

    @GET
    @Path("/ping")
    public Response ping () {

        return ResponseUtil.successResponse(null);
    }

    @GET
    @Path("/download/{fileId}")
    public Response getProjectFileDownloads (@HeaderParam("CF-Connecting-IP") String ip, @PathParam("fileId") long fileId) {

        final ProjectFilesEntity projectFile = Confluencia.FILE.findOneById(fileId);
        if (projectFile == null) {
            return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
        }

        final ProjectsEntity project = projectFile.getProject();

        final String gameSlug = project.getGame().getSlug();
        final String projectTypeSlug = project.getProjectType().getSlug();

        try {
            final URI uri = Constants.getNodeCDNFileURL(gameSlug, projectTypeSlug, project.getId(), projectFile.getId(), projectFile.getName());

            if (ip == null) {
                return Response.temporaryRedirect(uri).build();
            }

            final String saltedIp = AuthUtilities.getIP(ip);
            if (saltedIp != null) {
                if (!Confluencia.FILE.insertProjectFileDownloads(new ProjectFileDownloadsEntity(projectFile, saltedIp))) {
                    //return ErrorMessage.FAILED_INSERT_PROJECT_FILE_DOWNLOADS.respond();
                    return Response.temporaryRedirect(uri).build();
                }
            }

            return Response.temporaryRedirect(uri).build();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return ErrorMessage.THROWABLE.respond();
        }
    }
}
