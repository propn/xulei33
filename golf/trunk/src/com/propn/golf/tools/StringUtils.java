package com.propn.golf.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 字符串工具类
 * 
 * @author Administrator
 * 
 */
public class StringUtils {

    public static boolean isBlank(String str) {
        if (null == str || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        String line = null;
        StringBuilder sb = new StringBuilder(8192 * 20);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is), 8192 * 20);
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }
                reader = null;
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
