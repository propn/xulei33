package com.propn.golf.mvc.multipart;

import java.io.File;
import java.io.IOException;

/**
 * @author Thunder.Hsu
 * 
 */
public interface UpFile {

    public String getFileName();

    public String getContentType();

    public File WriteTO(String filePath) throws IOException;
}
