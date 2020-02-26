package com.diluv.api.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPIServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileReader {
    public static final Gson GSON = new GsonBuilder().create();

    public static <T> List<T> readJsonFolder (String folderName, Class<T> c) {

        final List<T> data = new ArrayList<>();
        final File folder = new File(TestUtil.class.getClassLoader().getResource("records/" + folderName).getFile());
        final File[] files = folder.listFiles();
        if (files != null) {
            for (final File file : files) {
                data.add(GSON.fromJson(FileReader.readFile(file), c));
            }
        }

        return data;
    }

    public static <T> T readJsonFile (String file, Class<T> c) {

        final URL url = FileReader.class.getClassLoader().getResource("records/" + file + ".json");
        if (url == null) {
            return null;
        }
        final File f = new File(url.getFile());

        final String data = FileReader.readFile(f);

        if (data == null) {
            return null;
        }
        return GSON.fromJson(data, c);
    }

    public static String readFile (File file) {

        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        }
        catch (final IOException e) {
            DiluvAPIServer.LOGGER.error("Failed to read file {}.", file.getName(), e);
            // TODO Throw exception(crash?)
        }
        return null;
    }
}
