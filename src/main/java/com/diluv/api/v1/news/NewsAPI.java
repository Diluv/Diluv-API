package com.diluv.api.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.diluv.api.utils.query.NewsQuery;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.cache.Cache;

import com.diluv.api.data.DataNewsPost;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.query.PaginationQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.NewsRecord;
import com.diluv.confluencia.database.sort.NewsSort;
import com.diluv.confluencia.database.sort.Sort;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
public class NewsAPI {

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/")
    public Response getNews (@Query NewsQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(NewsSort.NEW);

        final List<NewsRecord> newsRecords = DATABASE.newsDAO.findAll(page, limit, sort);
        final List<DataNewsPost> newsPosts = newsRecords.stream().map(DataNewsPost::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(newsPosts);
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{slug}")
    public Response getNewsBySlug (@PathParam("slug") String slug) {

        final NewsRecord newsRecord = DATABASE.newsDAO.findOneByNewsSlug(slug);

        if (newsRecord == null) {

            return ErrorMessage.NOT_FOUND_NEWS.respond();
        }

        return ResponseUtil.successResponse(new DataNewsPost(newsRecord));
    }
}