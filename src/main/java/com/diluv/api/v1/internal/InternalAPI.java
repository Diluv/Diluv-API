package com.diluv.api.v1.internal;


import com.diluv.api.utils.Constants;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.NodeCDNCommitsEntity;
import com.diluv.schoomp.Webhook;
import com.diluv.schoomp.message.Message;

import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.time.Instant;

@GZIP
@Path("/internal")
@Produces(MediaType.APPLICATION_JSON)
public class InternalAPI {

    private static final Webhook WEBHOOK = new Webhook(Constants.WEBHOOK_URL, "Diluv - API");

    @POST
    @Path("/nodecdn/{hash}")
    public Response postNodeCDNWebhook (@PathParam("hash") String hash) {

        NodeCDNCommitsEntity entity = Confluencia.SECURITY.findOneNodeCDNCommitsByHash(hash);
        if (entity == null) {
            return ErrorMessage.ENDPOINT_NOT_FOUND.respond();
        }

        entity.setCompleted(true);
        if (!Confluencia.update(entity)) {
            System.out.println("FAILED_UPDATE_NODECDN_COMMIT");
            // return ErrorMessage.FAILED_UPDATE_NODECDN_COMMIT.respond();
            return ErrorMessage.THROWABLE.respond();
        }

        int i = Confluencia.FILE.updateAllForRelease(entity.getCreatedAt());
        if (i == -1) {
            System.out.println("FAILED_UPDATE_PROJECT_FILE");
            // return ErrorMessage.FAILED_UPDATE_PROJECT_FILE.respond();
            return ErrorMessage.THROWABLE.respond();
        }

        if (i > 0 && Constants.WEBHOOK_URL != null) {
            try {
                Message discordMessage = new Message();
                discordMessage.setContent(Instant.now().toString() + ": NodeCDN fetched " + i + " files.");
                discordMessage.setUsername("Hilo");
                WEBHOOK.sendMessage(discordMessage);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
