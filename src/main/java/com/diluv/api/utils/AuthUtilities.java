package com.diluv.api.utils;

import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

public class AuthUtilities {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQSTUVWXYZ0123456789";
    private static final String NUMERIC = "0123456789";
    private static final Random random = new SecureRandom();

    public static String getBcrypt (char[] password) {

        final byte[] salt = new byte[16];
        random.nextBytes(salt);
        return OpenBSDBCrypt.generate(password, salt, Constants.BCRYPT_COST);
    }

    public static String getSecureRandomAlphanumeric (int length) {

        final char[] buf = new char[length];
        for (int i = 0; i < buf.length; ++i)
            buf[i] = ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length()));
        return new String(buf);
    }

    public static String getSecureRandomNumeric (int length) {

        final char[] buf = new char[length];
        for (int i = 0; i < buf.length; ++i)
            buf[i] = NUMERIC.charAt(random.nextInt(NUMERIC.length()));
        return new String(buf);
    }
}
