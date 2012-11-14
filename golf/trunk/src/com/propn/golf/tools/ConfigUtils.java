package com.propn.golf.tools;

import java.util.Locale;
import java.util.ResourceBundle;

import com.propn.golf.dao.sql.Constants;

public class ConfigUtils {

    private static ResourceBundle res = null;

    static {
        try {
            res = ResourceBundle.getBundle(Constants.PROPERTIES_FILE_NAME);
            // property.load(ClassLoader.getSystemResourceAsStream(Constants.PROPERTIES_FILE_NAME));
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件:" + Constants.PROPERTIES_FILE_NAME + " 出错!", e);
        }
    }

    public static String get(String key) {
        return res.getString(key);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Locale locale1 = new Locale("zh", "CN");
        // ResourceBundle resb1 = ResourceBundle.getBundle("golf", locale1);
        // System.out.println(resb1.getString("aaa"));
        //
        ResourceBundle resb2 = ResourceBundle.getBundle("golf", Locale.getDefault());
        System.out.println(resb2.getString("aaa"));
        System.out.println(get("aaa"));

        Locale locale3 = new Locale("en", "US");
        ResourceBundle resb3 = ResourceBundle.getBundle("golf", locale3);
        System.out.println(resb3.getString("aaa"));
    }
}
