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
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.GenericValidator;

import com.diluv.api.DiluvAPIServer;

public final class Constants {

    public static final PrivateKey PRIVATE_KEY = getPrivateKey("private.pem", "RSA", true);

    public static final int BCRYPT_COST = getValueOrDefault("BCRYPT_COST", 14);
    public static final String WEBSITE_URL = getValueOrDefault("WEBSITE_URL", "https://diluv.com");
    public static final Set<String> ALLOWED_ORIGINS = getValuesOrDefaultImmutable("ALLOWED_ORIGINS", Collections.singleton(WEBSITE_URL));

    // Database
    public static final String DB_HOSTNAME = getValueOrDefault("DB_HOSTNAME", "jdbc:mariadb://localhost:3306/diluv");
    public static final String DB_USERNAME = getValueOrDefault("DB_USERNAME", "root");
    public static final String DB_PASSWORD = getValueOrDefault("DB_PASSWORD", "");

    // CDN
    public static final String CDN_URL = getValueOrDefault("CDN_URL", "https://cdn.diluv.com");
    public static final String CDN_FOLDER = getValueOrDefault("CDN_FOLDER", "public");
    public static final String PROCESSING_FOLDER = getValueOrDefault("PROCESSING_FOLDER", "processing");

    // Emails
    public static final String POSTMARK_API_TOKEN = getValueOrDefault("POSTMARK_API_TOKEN", "POSTMARK_API_TEST");
    public static final String NOREPLY_EMAIL = getValueOrDefault("NOREPLY_EMAIL", "noreply@diluv.co");
    public static final String EMAIL_VERIFICATION = Constants.readResourceFileToString("/templates/email_verification.html", false);

    /**
     * Reads a string from an environment variable. If the variable can not be found the
     * program will terminate.
     *
     * @param name The name of the environment variable to read.
     * @return The value that was read.
     */
    private static String getRequiredValue (String name) {

        final String value = System.getenv(name);

        if (value == null) {

            DiluvAPIServer.LOGGER.error("Missing required environment variable {}.", name);
            System.exit(1);
        }

        return value;
    }

    /**
     * Reads a string from an environment variable. If the variable can not be found or is not
     * readable the default value will be returned.
     *
     * @param name The name of the environment variable.
     * @param defaultValue The default value to use when the variable is missing or can not be
     *     used.
     * @return The string that was read from the environment, or the default value if that can
     *     not be used.
     */
    private static String getValueOrDefault (String name, String defaultValue) {

        final String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }

    /**
     * Reads an immutable set of strings from an environment variable. If the variable can not
     * be found or is not mapped to a valid value the default values will be returned.
     *
     * @param name The name of the environment variable.
     * @param defaultValues The default values to use when the variable is missing or can not
     *     be used.
     * @return An immutable set of values read from the environment, or the default values if
     *     that can not be used.
     */
    private static Set<String> getValuesOrDefaultImmutable (String name, Set<String> defaultValues) {

        return Collections.unmodifiableSet(getValuesOrDefaults(name, defaultValues));
    }

    /**
     * Reads a set of strings from an environment variable. If the variable can not be found or
     * is not mapped to a valid value the default values will be returned.
     *
     * @param name The name of the environment variable.
     * @param defaultValues The default values to use when the variable is missing or can not
     *     be used.
     * @return A set of values read from the environment, or the default values if that can not
     *     be used.
     */
    private static Set<String> getValuesOrDefaults (String name, Set<String> defaultValues) {

        final String value = System.getenv(name);
        return value == null ? defaultValues : Arrays.stream(value.split(",")).collect(Collectors.toSet());
    }

    /**
     * Reads an integer from a system environment variable. If the variable can not be found or
     * is not mapped to a valid integer a default value will be returned.
     *
     * @param name The name of the environment variable.
     * @param defaultValue The default value to use when the variable is missing or can not be
     *     used.
     * @return The integer variable, or the default value if it is not usable.
     */
    private static int getValueOrDefault (String name, int defaultValue) {

        final String value = System.getenv(name);
        return value == null || !GenericValidator.isInt(value) ? defaultValue : Integer.parseInt(value);
    }

    /**
     * Reads a private key from a file. If the key can not be read an error will be logged and
     * a null value will be returned.
     *
     * @param fileLocation The location of the file to read.
     * @param required Whether or not this is a required key. If true the program will
     *     terminate on a null value.
     * @return The private key read from the provided file. If no key can be loaded this will
     *     return null.
     */
    @Nullable
    public static PrivateKey getPrivateKey (String fileLocation, String algorithm, boolean required) {

        try {

            String privateKey = readFileToString(fileLocation, true);

            if (privateKey != null) {

                privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "");
                privateKey = privateKey.replace("-----END PRIVATE KEY-----", "");

                final KeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
                final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

                return keyFactory.generatePrivate(spec);
            }
        }

        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {

            DiluvAPIServer.LOGGER.error("Failed to read valid private key {} with algorithm {}.", fileLocation, algorithm, e);
        }

        if (required) {

            System.exit(1);
        }

        return null;
    }

    /**
     * Attempts to read the contents of a file as a string. If the file can not be read for any
     * reason an error will be logged and a null value will be returned.
     *
     * @param fileLocation The location of the file to read.
     * @param stripNewline Whether or not newline characters should be stripped from the file.
     * @return The contents of the file read as a string. If the file could not be read null
     *     will be returned.
     */
    @Nullable
    public static String readResourceFileToString (String fileLocation, boolean stripNewline) {

        try {
            String contents = IOUtils.resourceToString(fileLocation, Charset.defaultCharset());

            if (stripNewline) {

                contents = contents.replaceAll("\\r|\\n", "");
            }

            return contents;
        }

        catch (final IOException e) {

            DiluvAPIServer.LOGGER.error("Failed to read file {} as string.", fileLocation, e);
        }

        return null;
    }

    /**
     * Attempts to read the contents of a file as a string. If the file can not be read for any
     * reason an error will be logged and a null value will be returned.
     *
     * @param fileLocation The location of the file to read.
     * @param stripNewline Whether or not newline characters should be stripped from the file.
     * @return The contents of the file read as a string. If the file could not be read null
     *     will be returned.
     */
    @Nullable
    public static String readFileToString (String fileLocation, boolean stripNewline) {

        try (final FileInputStream fileInputStream = new FileInputStream(fileLocation)) {

            String contents = IOUtils.toString(fileInputStream, Charset.defaultCharset());

            if (stripNewline) {

                contents = contents.replaceAll("\\r|\\n", "");
            }

            return contents;
        }

        catch (final IOException e) {

            DiluvAPIServer.LOGGER.error("Failed to read file {} as string.", fileLocation, e);
        }

        return null;
    }

    public static String getUserAvatar (String username) {

        return String.format("%s/users/%s/avatar.png", CDN_URL, username);
    }
}
