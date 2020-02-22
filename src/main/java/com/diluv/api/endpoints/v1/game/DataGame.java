package com.diluv.api.endpoints.v1.game;

import com.diluv.confluencia.database.record.GameRecord;

public class DataGame {
    private final String slug;
    private final String name;
    private final String url;
    
    public DataGame(GameRecord rs) {
        
        this.slug = rs.getSlug();
        this.name = rs.getName();
        this.url = rs.getUrl();
    }
    
    public String getSlug () {
        
        return this.slug;
    }
    
    public String getName () {
        
        return this.name;
    }
    
    public String getUrl () {
        
        return this.url;
    }
}
