package com.diluv.api.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPI;

public class FileReader {

    public static <T> List<T> readJsonFolder (String folderName, Class<T> c) {

        List<T> data = new ArrayList<>();
        File folder = new File(TestUtil.class.getClassLoader().getResource("records/" + folderName).getFile());
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                data.add(DiluvAPI.GSON.fromJson(FileReader.readFile(file), c));
            }
        }

        return data;
    }

    public static <T> T readJsonFile (String file, Class<T> c) {

        URL url = FileReader.class.getClassLoader().getResource("records/" + file + ".json");
        if (url == null)
            return null;
        File f = new File(url.getFile());

        String data = FileReader.readFile(f);

        if (data == null)
            return null;
        return DiluvAPI.GSON.fromJson(data, c);
    }

    public static String readFile (File file) {

        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        }
        catch (IOException e) {
            DiluvAPI.LOGGER.error("Failed to read file {}.", file.getName(), e);
            //TODO Throw exception(crash?)
        }
        return null;
    }
}
