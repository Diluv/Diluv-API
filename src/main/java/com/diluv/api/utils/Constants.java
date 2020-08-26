package com.diluv.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.data.DataImage;
import com.diluv.api.data.DataImageSource;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public final class Constants {

    public static final String ENV = getValueOrDefault("ENVIRONMENT", "PRODUCTION");

    public static final String AUTH_BASE_URL = getValueOrDefault("AUTH_BASE_URL", "https://auth.diluv.com");
    public static final ConfigurableJWTProcessor<SecurityContext> JWT_PROCESSOR = getJWTProcessor();

    public static final String WEBSITE_URL = getValueOrDefault("WEBSITE_URL", "https://diluv.com");
    public static final String API_URL = getValueOrDefault("WEBSITE_URL", "https://api.diluv.com");
    public static final Set<String> ALLOWED_ORIGINS = getValuesOrDefaultImmutable("ALLOWED_ORIGINS", Collections.singleton(WEBSITE_URL));

    public static final int BCRYPT_COST = getValueOrDefault("BCRYPT_COST", 14);
    public static final Salt SALT = getFileSalt();

    // Database
    public static final String DB_HOSTNAME = getValueOrDefault("DB_HOSTNAME", "jdbc:mariadb://localhost:3306/diluv");
    public static final String DB_USERNAME = getValueOrDefault("DB_USERNAME", "root");
    public static final String DB_PASSWORD = getValueOrDefault("DB_PASSWORD", "");

    // CDN
    public static final String CDN_URL = getValueOrDefault("CDN_URL", "https://download.nodecdn.net/containers/diluv");
    public static final String CDN_FOLDER = getValueOrDefault("CDN_FOLDER", "public");
    public static final String PROCESSING_FOLDER = getValueOrDefault("PROCESSING_FOLDER", "processing");

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

    @Nullable
    public static ConfigurableJWTProcessor<SecurityContext> getJWTProcessor () {

        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

            jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("at+jwt")));
            jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(AUTH_BASE_URL).build(),
                new HashSet<>(Arrays.asList("sub", /*"iat",*/ "exp"/*, "jti"*/))));

            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(AUTH_BASE_URL + "/.well-known/openid-configuration/jwks"));

            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);

            jwtProcessor.setJWSKeySelector(keySelector);

            return jwtProcessor;
        }
        catch (IOException e) {
            DiluvAPIServer.LOGGER.catching(e);
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

    public static boolean isDevelopment () {

        return "DEVELOPMENT".equalsIgnoreCase(ENV);
    }

    public static String getUserAvatar (String username) {

        return String.format("%s/users/%s/avatar.png", CDN_URL, username);
    }

    public static String getProjectLogo (ProjectsEntity project) {

        if (isDevelopment()) {
            return "https://images.placeholders.dev/?width=400&height=400";
        }
        final String gameSlug = project.getGame().getSlug();
        final String projectTypeSlug = project.getProjectType().getSlug();
        return String.format("%s/games/%s/%s/%d/logo.png", CDN_URL, gameSlug, projectTypeSlug, project.getId());
    }

    public static DataImage getGameLogoURL (String gameSlug) {

        if (isDevelopment()) {
            final String url = "https://images.placeholders.dev/?width=250&height=130";
            return new DataImage(new DataImageSource(url + "&text=fallback", "image/png"), new DataImageSource[]{new DataImageSource(url + "&text=" + gameSlug, "image/svg+xml")});
        }

        final String baseURL = String.format("%s/games/%s/logo", CDN_URL, gameSlug);
        final String pngURL = baseURL + ".png";
        return new DataImage(new DataImageSource(pngURL, "image/png"), new DataImageSource[]{
            new DataImageSource(baseURL + ".webp", "image/webp")
        });
    }

    public static String getFileURL (long fileId) {

        return String.format("%s/v1/download/%d", Constants.API_URL, fileId);
    }

    public static URI getNodeCDNFileURL (String gameSlug, String projectTypeSlug, long projectId, long fileId, String fileName) throws URISyntaxException {

        return new URI(String.format("%s/games/%s/%s/%d/%d/%s", Constants.CDN_URL, gameSlug, projectTypeSlug, projectId, fileId, fileName));
    }

    public static Salt getSalt () {

        try {
            int week = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            if (SALT.getWeek() != week) {
                SALT.setData(AuthUtilities.writeSalt(week));
                SALT.setWeek(week);
            }

            return SALT;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Salt getFileSalt () {

        try {
            int week = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            File file = new File("salt.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null) {
                        String[] s = line.split(":");
                        if (s.length == 2) {
                            String salt = s[0];
                            Integer fileWeek = GenericTypeValidator.formatInt(s[1]);
                            if (!GenericValidator.isBlankOrNull(salt) && week == fileWeek) {
                                return new Salt(salt, week);
                            }
                        }
                    }
                }
            }
            else {
                file.createNewFile();
            }
            return new Salt(AuthUtilities.writeSalt(week), week);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
