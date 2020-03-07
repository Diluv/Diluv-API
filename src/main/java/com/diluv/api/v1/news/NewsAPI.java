package com.diluv.api.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.data.DataNewsPost;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.NewsRecord;
import com.diluv.confluencia.utils.Pagination;

import org.jboss.resteasy.annotations.cache.Cache;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/news")
public class NewsAPI {

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf (@QueryParam("cursor") String queryCursor, @QueryParam("limit") int queryLimit) {

        Pagination pagination = Pagination.getPagination(queryCursor);
        int limit = Pagination.getLimit(queryLimit);

        final List<NewsRecord> newsRecords = DATABASE.newsDAO.findAll(pagination, limit + 1);
        final List<DataNewsPost> newsPosts = newsRecords.stream().limit(limit).map(DataNewsPost::new).collect(Collectors.toList());

        return ResponseUtil.successResponsePagination(newsPosts, newsRecords.size() > limit ? new Pagination(limit + pagination.offset).getCursor() : null);
    }

    @Cache(maxAge = 300, mustRevalidate = true)
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