package com.diluv.api.endpoints.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.aaa.RoutingHandlerPlus;
import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.record.NewsRecord;
import io.undertow.server.HttpServerExchange;

public class NewsAPI extends RoutingHandlerPlus {

    private final NewsDAO newsDAO;

    public NewsAPI (NewsDAO newsDAO) {

        this.newsDAO = newsDAO;
        this.get("/", this::getNews);
        this.get("/{slug}", "slug", this::getNewsBySlug);
    }

    private IResponse getNews (HttpServerExchange exchange) {

        final List<NewsRecord> newsRecords = this.newsDAO.findAll();
        final List<DataNewsPost> games = newsRecords.stream().map(DataNewsPost::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private IResponse getNewsBySlug (HttpServerExchange exchange, String newsSlug) {

        final NewsRecord newsRecord = this.newsDAO.findOneByNewsSlug(newsSlug);
        if (newsRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_NEWS);
        }
        return ResponseUtil.successResponse(exchange, new DataNewsPost(newsRecord));
    }
}