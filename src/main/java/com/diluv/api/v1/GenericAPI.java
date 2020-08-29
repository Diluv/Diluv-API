package com.diluv.api.v1;

import com.diluv.api.utils.AuthUtilities;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.ProjectFileDownloadsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;

import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Path("/download/{gameSlug}/{projectTypeSlug}/{projectId}/{fileId}/{projectFileName}")
    public Response getProjectFileDownloads (@HeaderParam("CF-Connecting-IP") String ip, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectId") long projectId, @PathParam("fileId") long fileId, @PathParam("projectFileName") String projectFileName) {

        try {
            final URI uri = Constants.getNodeCDNFileURL(gameSlug, projectTypeSlug, projectId, fileId, projectFileName);

            if (ip == null) {
                return Response.temporaryRedirect(uri).build();
            }

            final String saltedIp = AuthUtilities.getIP(ip);
            if (saltedIp != null) {
                if (!Confluencia.FILE.insertProjectFileDownloads(new ProjectFileDownloadsEntity(new ProjectFilesEntity(fileId), saltedIp))) {
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
