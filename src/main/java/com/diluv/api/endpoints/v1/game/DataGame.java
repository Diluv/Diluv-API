package com.diluv.api.endpoints.v1.game;

import com.diluv.confluencia.database.record.GameRecord;

/**
 * Represents the data for a game that we support.
 */
public class DataGame {
    
    /**
     * The slug for the game. This is a unique string that identifies the game in URLs and API
     * requests.
     */
    private final String slug;
    
    /**
     * The display name for the game.
     */
    private final String name;
    
    /**
     * A URL that links to the official home page of the game.
     */
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
