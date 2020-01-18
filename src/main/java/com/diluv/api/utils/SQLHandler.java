package com.diluv.api.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPI;

public final class SQLHandler {
    private SQLHandler () {

    }

    @Nullable
    public static String readFile (String filename) {

        try {
        	
            File f = new File(SQLHandler.class.getClassLoader().getResource("db/query/" + filename + ".sql").getFile());
            return FileUtils.readFileToString(f, Charset.defaultCharset());
        }
        
        catch (IOException e) {
        	
        	DiluvAPI.LOGGER.error("Failed to read sql script file {}.", filename, e);
        }
        
        return null;
    }
}