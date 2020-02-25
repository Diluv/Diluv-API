package com.diluv.api.database;

import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.record.NewsRecord;

public class NewsTestDatabase implements NewsDAO {
    
    private final List<NewsRecord> newsRecord;
    
    public NewsTestDatabase() {
        
        this.newsRecord = FileReader.readJsonFolder("news", NewsRecord.class);
    }
    
    @Override
    public List<NewsRecord> findAll () {
        
        return this.newsRecord;
    }
    
    @Override
    public NewsRecord findOneByNewsSlug (String slug) {
        
        for (final NewsRecord userRecord : this.newsRecord) {
            if (userRecord.getSlug().equalsIgnoreCase(slug)) {
                return userRecord;
            }
        }
        return null;
    }
}
