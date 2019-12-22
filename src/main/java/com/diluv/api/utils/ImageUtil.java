package com.diluv.api.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static BufferedImage isValidImage (String url) {

        try {
            return ImageIO.read(new URL(url));
        }
        catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage isValidImage (InputStream inputStream) {

        try {
            return ImageIO.read(inputStream);
        }
        catch (IOException e) {
            return null;
        }
    }

    public static boolean saveImage (BufferedImage image, File file) {

        file.getParentFile().mkdirs();
        try {
            return ImageIO.write(image, "png", file);
        }
        catch (IOException ignored) {
            return false;
        }
    }
}
