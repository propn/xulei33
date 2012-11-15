package com.propn.golf.tools;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.dao.sql.Constants;

public class ParamsUtils {

    private static final Logger logger = LoggerFactory.getLogger(ParamsUtils.class);

    private static final Properties property = new Properties();

    static {
        try {
            logger.debug("Load properties");
            property.load(ClassLoader.getSystemResourceAsStream(Constants.PROPERTIES_FILE_NAME));
        } catch (Exception e) {

        }
    }

    public static Properties getProperty() {
        return property;
    }

    public static String getParamValue(String key) throws Exception {
        return getProperty().getProperty(key);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println(getParamValue("DEFAULT_JNDI"));
            System.out.println(getParamValue("url"));
            System.out.println(getParamValue("driver"));
            System.out.println(getParamValue("username"));
            System.out.println(getParamValue("password"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
