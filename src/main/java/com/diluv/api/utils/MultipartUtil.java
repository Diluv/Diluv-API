package com.diluv.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class MultipartUtil {

    public static Path saveFile (InputStream stream) {

        try {
            Path file = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")), "diluv", "upload");
            FileUtils.copyInputStreamToFile(stream, file.toFile());

            return file;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
