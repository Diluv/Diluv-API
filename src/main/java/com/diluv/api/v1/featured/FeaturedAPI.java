package com.diluv.api.v1.featured;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataCategory;
import com.diluv.api.data.DataGame;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.CategoryRecord;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectRecord;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/featured")
public class FeaturedAPI {

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/games")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGames () {

        final List<GameRecord> gameRecords = DATABASE.gameDAO.findFeaturedGames();
        final List<DataGame> games = gameRecords.stream().map(DataGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(games);
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects () {

        final List<ProjectRecord> projectRecords = DATABASE.projectDAO.findFeaturedProjects();

        final List<DataBaseProject> projects = projectRecords.stream().map(projectRecord -> {
            final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByProjectId(projectRecord.getId());
            List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());
            return new DataBaseProject(projectRecord, categories);
        }).collect(Collectors.toList());
        return ResponseUtil.successResponse(projects);
    }
}
