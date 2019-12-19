package com.diluv.api.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class Constants {
    public static final PrivateKey PRIVATE_KEY = getPrivateKey("private.pem");

    public static final String DB_HOSTNAME = getValueOrDefault("DB_HOSTNAME", "jdbc:mariadb://localhost:3306/diluv");
    public static final String DB_USERNAME = getValueOrDefault("DB_USERNAME", "root");
    public static final String DB_PASSWORD = getValueOrDefault("DB_PASSWORD", "");

    private static final Logger LOGGER = Logger.getLogger(Constants.class.getName());

    private Constants () {

    }

    private static String getRequiredValue (String env) {

        String value = System.getenv(env);
        if (value == null) {
            LOGGER.severe("Missing env variable");
            System.exit(1);
        }
        return value;
    }

    private static String getValueOrDefault (String env, String defaultValue) {

        String value = System.getenv(env);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static PrivateKey getPrivateKey (String fileLocation) {

        try {
            String privateKey = getKey(fileLocation);
            privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "");
            privateKey = privateKey.replace("-----END PRIVATE KEY-----", "");
            KeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        }
        catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Private Key", e);
        }

        System.exit(1); //TODO Handle better
        return null;
    }

    public static String getKey (String fileLocation) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(fileLocation);
        return IOUtils.toString(fileInputStream, Charset.defaultCharset()).replaceAll("\n", "").replaceAll("\r", "");
    }
}
