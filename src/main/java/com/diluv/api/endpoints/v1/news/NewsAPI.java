package com.diluv.api.endpoints.v1.news;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.news.domain.NewsDomain;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.record.NewsRecord;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class NewsAPI extends RoutingHandler {
    
    private final NewsDAO newsDAO;
    
    public NewsAPI(NewsDAO newsDAO) {
        
        this.newsDAO = newsDAO;
        this.get("/", this::getNews);
        this.get("/{news_slug}", this::getNewsBySlug);
    }
    
    private Domain getNews (HttpServerExchange exchange) {
        
        final List<NewsRecord> newsRecords = this.newsDAO.findAll();
        final List<NewsDomain> games = newsRecords.stream().map(NewsDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }
    
    private Domain getNewsBySlug (HttpServerExchange exchange) {
        
        final String newsSlug = RequestUtil.getParam(exchange, "news_slug");
        if (newsSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NEWS_INVALID_SLUG);
        }
        
        final NewsRecord newsRecord = this.newsDAO.findOneByNewsSlug(newsSlug);
        if (newsRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_NEWS);
        }
        return ResponseUtil.successResponse(exchange, new NewsDomain(newsRecord));
    }
}