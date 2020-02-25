package com.diluv.api.utils;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

public class FileUtil {
    
    public static String getSHA512 (FormData.FileItem fileItem) {
        
        try (BufferedInputStream bis = new BufferedInputStream(fileItem.getInputStream())) {
            
            return DigestUtils.sha512Hex(bis);
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    public static Long getSize (FormData.FileItem fileItem) throws MultiPartParserDefinition.FileTooLargeException {
        
        try {
            return fileItem.getFileSize();
        }
        catch (final IOException e) {
            throw new MultiPartParserDefinition.FileTooLargeException();
        }
    }
}
