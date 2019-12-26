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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.GenericValidator;

public class Constants {
    public static final PrivateKey PRIVATE_KEY = getPrivateKey("private.pem");

    public static final String DB_HOSTNAME = getValueOrDefault("DB_HOSTNAME", "jdbc:mariadb://localhost:3306/diluv");
    public static final String DB_USERNAME = getValueOrDefault("DB_USERNAME", "root");
    public static final String DB_PASSWORD = getValueOrDefault("DB_PASSWORD", "");
    public static final String MEDIA_FOLDER = getValueOrDefault("MEDIA_FOLDER", "media");
    public static final Set<String> ALLOWED_ORIGINS = getValuesOrDefaults("ALLOWED_ORIGINS", Collections.emptySet());
    public static final int BCRYPT_COST = getValueOrDefault("BCRYPT_COST", 14);

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

    private static Set<String> getValuesOrDefaults (String env, Set<String> defaultValues) {

        String value = System.getenv(env);
        if (value == null) {
            return defaultValues;
        }
        return Arrays.stream(value.split(",")).collect(Collectors.toSet());
    }

    private static int getValueOrDefault (String env, int defaultValue) {

        String value = System.getenv(env);
        if (value == null || !GenericValidator.isInt(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
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
        catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
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
