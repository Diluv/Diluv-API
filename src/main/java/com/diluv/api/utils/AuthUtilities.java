package com.diluv.api.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class AuthUtilities {
    private static final String symbols = "ABCDEFGHIJKLMNOPQSTUVWXYZ0123456789";
    private static final Random random = new SecureRandom();
    private static final File file = new File("salt.txt");

    public static String getBcrypt (char[] password) {

        final byte[] salt = new byte[16];
        random.nextBytes(salt);
        return OpenBSDBCrypt.generate(password, salt, Constants.BCRYPT_COST);
    }

    public static String getSecureRandomAlphanumeric (int length) {

        final char[] buf = new char[length];
        for (int i = 0; i < buf.length; ++i)
            buf[i] = symbols.charAt(random.nextInt(symbols.length()));
        return new String(buf);
    }

    public static String writeSalt (int week) throws IOException {

        String salt = getSecureRandomAlphanumeric(32);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(salt + ":" + week);
            fileWriter.flush();
        }
        return salt;
    }

    public static String getIP (String ipAddr) {

        Salt salt = Constants.getSalt();
        if (salt == null) {
            // Failed to securely store salt, don't record, don't increment
            return null;
        }
        MessageDigest digest = new SHA3.Digest512();
        digest.update(StringUtils.getBytesUtf8(salt.getData()));
        digest.update(StringUtils.getBytesUtf8(ipAddr));
        return Hex.toHexString(digest.digest());
    }
}
