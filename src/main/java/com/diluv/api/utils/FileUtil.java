package com.diluv.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.james.mime4j.io.LimitedInputStream;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import com.diluv.api.DiluvAPIServer;

public class FileUtil {

    public static String writeFile (InputStream input, long limit, File destination) {

        try (DigestInputStream hashStream = new DigestInputStream(input, new SHA3.Digest512());
             LimitedInputStream in = new LimitedInputStream(hashStream, limit)) {

            FileUtils.copyInputStreamToFile(in, destination);
            return Hex.toHexString(hashStream.getMessageDigest().digest());
        }

        catch (IOException e) {

            return null;
        }
    }

    @Nullable
    public static File getTempFile (long project, String fileName) {

        try {

            final File tempFolder = Files.createTempDirectory(Long.toString(project)).toFile();
            return new File(tempFolder, fileName);
        }

        catch (IOException e) {

            DiluvAPIServer.LOGGER.catching(e);
            return null;
        }
    }

    public static File getOutputLocation (String game, String type, long project, long id, String inputFileName) {

        return new File(
            Constants.PROCESSING_FOLDER, game + "/" + type + "/" + project + "/" + id + "/" + inputFileName);
    }
}