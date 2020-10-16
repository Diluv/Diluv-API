package com.diluv.api.v1.featured;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataFeatured;
import com.diluv.api.data.DataGame;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.Confluencia;

@GZIP
@Path("/featured")
@Produces(MediaType.APPLICATION_JSON)
public class FeaturedAPI {

    @GET
    @Path("/")
    public Response getFeatured () {

        return Confluencia.getTransaction(session -> {
            final List<DataGame> games = Confluencia.GAME.findFeaturedGames(session)
                .stream()
                .map(DataGame::new)
                .collect(Collectors.toList());

            final List<DataBaseProject> projects = Confluencia.PROJECT.findFeaturedProjects(session)
                .stream()
                .map(DataBaseProject::new)
                .collect(Collectors.toList());

            final long projectCount = Confluencia.PROJECT.countAllByGameSlug(session, "");
            final long userCount = Confluencia.USER.countAll(session);

            return ResponseUtil.successResponse(new DataFeatured(games, projects, projectCount, userCount));
        });
    }
}
