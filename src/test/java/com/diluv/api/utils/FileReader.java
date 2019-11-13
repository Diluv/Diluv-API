package com.diluv.api.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPI;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FileReader {

    public static <T> List<T> readJsonFolder (String folderName, Class<T> c) {

        List<T> data = new ArrayList<>();
        try {
            File folder = new File(SQLHandler.class.getClassLoader().getResource(folderName).getFile());
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(DiluvAPI.MAPPER.readValue(FileReader.readFile(file), c));
                }
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static String readFile (File file) {

        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        }
        catch (IOException e) {
            e.printStackTrace();
            //TODO Throw exception(crash?)
        }
        return null;
    }
}
