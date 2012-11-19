package com.propn.golf.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author Administrator
 * 
 */
public class StringUtils {

    public static String list2Stirng(List<?> args) {
        if (null == args || args.isEmpty()) {
            return null;
        }
        StringBuffer rst = new StringBuffer();
        for (Object obj : args) {
            rst.append(obj).append(",");
        }
        return rst.substring(0, rst.length() - 1);
    }

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

    /**
     * 驼峰转下划线
     * 
     * @param param
     * @return
     */
    public static String camel4underline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }
        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰
     * 
     * @param columnName
     * @return
     */
    public static String underline4camel(String columnName) {

        if (columnName == null || columnName.equals("")) {
            return "";
        }

        if (columnName.indexOf("_") < 0) {
            return columnName.toLowerCase();
        }

        columnName = columnName.toLowerCase();
        Pattern p = Pattern.compile("_[a-z]");
        Matcher mc = p.matcher(columnName);
        StringBuilder builder = new StringBuilder(columnName);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, mc.group().toUpperCase());
        }
        return builder.toString().replaceAll("_", "");
    }

}
