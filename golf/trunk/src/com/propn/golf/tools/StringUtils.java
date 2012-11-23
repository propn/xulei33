package com.propn.golf.tools;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;

/**
 * 字符串工具类
 * 
 * @author Administrator
 * 
 */
public class StringUtils {

    public static final String charsetName = "UTF-8";
    public static final int BUFFER_SIZE = 8192;

    public static boolean isBlank(String str) {
        if (null == str || str.isEmpty() || str.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

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

    /**
     * Read bytes from an input stream and write them to an output stream.
     * 
     * @param in the input stream to read from.
     * @param out the output stream to write to.
     * @throws IOException if there is an error reading or writing bytes.
     */
    public static final void writeTo(InputStream in, OutputStream out) throws IOException {
        int read;
        final byte[] data = new byte[BUFFER_SIZE];
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }

    /**
     * Read characters from an input stream and write them to an output stream.
     * 
     * @param in the reader to read from.
     * @param out the writer to write to.
     * @throws IOException if there is an error reading or writing characters.
     */
    public static final void writeTo(Reader in, Writer out) throws IOException {
        int read;
        final char[] data = new char[BUFFER_SIZE];
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }

    public static ByteArrayInputStream servletInputStream2ByteArrayInputStream(ServletInputStream in)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int i = 0;
        // inputStream 转 byte
        while ((i = in.read(b, 0, 1024)) > 0) {
            out.write(b, 0, i);
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(out.toByteArray());
        return bin;
    }

    public static String servletInputStream2String(ServletInputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int i = 0;
        // inputStream 转 byte
        while ((i = in.read(b, 0, 1024)) > 0) {
            out.write(b, 0, i);
        }
        in.reset();
        return new String(out.toByteArray(), charsetName);
    }

    public static String byteArrayInputStream2String(ByteArrayInputStream bin) throws IOException {
        byte[] b = new byte[bin.available()];
        bin.read(b, 0, bin.available());
        String entity = new String(b, charsetName);
        bin.reset();
        return entity;
    }

    /**
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream in) throws IOException {
        Reader reader = new InputStreamReader(in, charsetName);
        StringBuilder sb = new StringBuilder();
        char[] c = new char[BUFFER_SIZE];
        int l;
        while ((l = reader.read(c)) != -1) {
            sb.append(c, 0, l);
        }
        return sb.toString();
    }

    /**
     * Convert a string to bytes and write those bytes to an output stream.
     * 
     * @param s the string to convert to bytes.
     * @param out the output stream to write to.
     * @param type the media type that determines the character set defining how to decode bytes to characters.
     * @throws IOException
     */
    public static final void writeToAsString(String s, OutputStream out) throws IOException {
        Writer osw = new BufferedWriter(new OutputStreamWriter(out, charsetName));
        osw.write(s);
        osw.flush();
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
