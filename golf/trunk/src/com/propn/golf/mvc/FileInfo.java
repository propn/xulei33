/**
 * 
 */
package com.propn.golf.mvc;

import java.io.File;
import java.io.IOException;

/**
 * @author Thunder.Hsu
 * 
 */
public interface FileInfo {

    public String getFileName();

    public String getContentType();

    public File WriteTO(String filePath) throws IOException;

}
