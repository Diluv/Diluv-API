package com.diluv.api.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String hex (byte[] array) {
        
        final StringBuilder sb = new StringBuilder();
        for (final byte b : array) {
            sb.append(Integer.toHexString(b & 0xFF | 0x100), 1, 3);
        }
        return sb.toString();
    }
    
    public static String md5Hex (String message) {
        
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
