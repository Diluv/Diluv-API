package com.diluv.api.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.diluv.api.data.DataNewsPost;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.NewsRecord;

import org.jboss.resteasy.annotations.GZIP;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/news")
public class NewsAPI {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf () {

        final List<NewsRecord> newsRecords = DATABASE.newsDAO.findAll();
        final List<DataNewsPost> newsPosts = newsRecords.stream().map(DataNewsPost::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(newsPosts);
    }

    @GET
    @Path("/{slug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser (@PathParam("slug") String slug) {

        final NewsRecord newsRecord = DATABASE.newsDAO.findOneByNewsSlug(slug);

        if (newsRecord == null) {

            return ErrorMessage.NOT_FOUND_NEWS.respond();
        }

        return ResponseUtil.successResponse(new DataNewsPost(newsRecord));
    }
}