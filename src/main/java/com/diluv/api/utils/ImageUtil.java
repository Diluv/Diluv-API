package com.diluv.api.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

public class ImageUtil {

    public static Long getSize (FormData.FileItem fileItem) throws MultiPartParserDefinition.FileTooLargeException {

        try {
            return fileItem.getFileSize();
        }
        catch (IOException e) {
            throw new MultiPartParserDefinition.FileTooLargeException();
        }
    }

    public static BufferedImage isValidImage (String url) {

        try {
            return ImageIO.read(new URL(url));
        }
        catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage isValidImage (FormData.FileItem fileItem) {

        try {
            return ImageIO.read(fileItem.getInputStream());
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
