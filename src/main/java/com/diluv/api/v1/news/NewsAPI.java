package com.diluv.api.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;

import com.diluv.api.data.DataNewsPost;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.query.NewsQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.NewsEntity;
import com.diluv.confluencia.database.sort.NewsSort;
import com.diluv.confluencia.database.sort.Sort;

@GZIP
@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
public class NewsAPI {

    @GET
    @Path("/")
    public Response getNews (@Query NewsQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(NewsSort.NEW);

        return Confluencia.getTransaction(session -> {
            final List<NewsEntity> newsRecords = Confluencia.NEWS.findAll(session, page, limit, sort);
            final List<DataNewsPost> newsPosts =
                newsRecords.stream().map(DataNewsPost::new).collect(Collectors.toList());

            return ResponseUtil.successResponse(newsPosts);
        });
    }

    @GET
    @Path("/{slug}")
    public Response getNewsBySlug (@PathParam("slug") String slug) {

        return Confluencia.getTransaction(session -> {
            final NewsEntity newsRecord = Confluencia.NEWS.findOneByNewsSlug(session, slug);

            if (newsRecord == null) {

                return ErrorMessage.NOT_FOUND_NEWS.respond();
            }

            return ResponseUtil.successResponse(new DataNewsPost(newsRecord));
        });
    }
}