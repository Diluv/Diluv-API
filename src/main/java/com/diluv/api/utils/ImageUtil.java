package com.diluv.api.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.james.mime4j.io.LimitedInputStream;

public class ImageUtil {

    public static BufferedImage isValidImage (String url) {

        try {
            return ImageIO.read(new URL(url));
        }
        catch (final IOException e) {
            return null;
        }
    }

    public static BufferedImage isValidImage (InputStream inputStream, long limit) {

        try {
            return ImageIO.read(new LimitedInputStream(inputStream, limit));
        }
        catch (final IOException e) {
            return null;
        }
    }

    public static boolean savePNG (BufferedImage image, File file) {

        return saveImage(image, "png", file);
    }

    public static boolean saveWebp (BufferedImage image, File file) {

        return saveImage(image, "webp", file);
    }

    private static boolean saveImage (BufferedImage image, String type, File file) {

        file.getParentFile().mkdirs();
        try {
            return ImageIO.write(image, type, file);
        }
        catch (final IOException ignored) {
            return false;
        }
    }
}
