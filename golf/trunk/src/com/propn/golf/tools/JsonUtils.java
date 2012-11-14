package com.propn.golf.tools;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 封装GSON 提供Java对象到JSON的序列化和反序列化
 * 
 * @author Thunder.hsu
 */
public class JsonUtils {

    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_JSON_ARRAY = "[]";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    public JsonUtils() {
        super();
    }

    public static String toJson(Object target, Type targetType, boolean isSerializeNulls, Double version,
            String datePattern, boolean excludesFieldsWithoutExpose) {
        if (target == null)
            return EMPTY_JSON;
        GsonBuilder builder = new GsonBuilder();
        if (isSerializeNulls)
            builder.serializeNulls();
        if (version != null)
            builder.setVersion(version.doubleValue());
        if (StringUtils.isBlank(datePattern))
            datePattern = DEFAULT_DATE_PATTERN;
        builder.setDateFormat(datePattern);
        if (!excludesFieldsWithoutExpose) {
            builder.excludeFieldsWithoutExposeAnnotation();
        }
        return toJson(target, targetType, builder);
    }

    public static String toJson(Object target) {
        return toJson(target, null, false, null, null, true);
    }

    public static String toJson(Object target, String datePattern) {
        return toJson(target, null, false, null, datePattern, true);
    }

    public static String toJson(Object target, Double version) {
        return toJson(target, null, false, version, null, true);
    }

    public static String toJson(Object target, boolean excludesFieldsWithoutExpose) {
        return toJson(target, null, false, null, null, excludesFieldsWithoutExpose);
    }

    public static String toJson(Object target, Double version, boolean excludesFieldsWithoutExpose) {
        return toJson(target, null, false, version, null, excludesFieldsWithoutExpose);
    }

    public static String toJson(Object target, Type targetType) {
        return toJson(target, targetType, false, null, null, true);
    }

    public static String toJson(Object target, Type targetType, Double version) {
        return toJson(target, targetType, false, version, null, true);
    }

    public static String toJson(Object target, Type targetType, boolean excludesFieldsWithoutExpose) {
        return toJson(target, targetType, false, null, null, excludesFieldsWithoutExpose);
    }

    public static String toJson(Object target, Type targetType, Double version, boolean excludesFieldsWithoutExpose) {
        return toJson(target, targetType, false, version, null, excludesFieldsWithoutExpose);
    }

    public static <T> T fromJson(String json, TypeToken<T> token, String datePattern) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        if (StringUtils.isBlank(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        Gson gson = builder.create();
        try {
            return gson.fromJson(json, token.getType());
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeToken<T> token) {
        return fromJson(json, token, null);
    }

    public static <T> T fromJson(String json, Class<T> clazz, String datePattern) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        if (StringUtils.isBlank(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        Gson gson = builder.create();
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, null);
    }

    public static String toJson(Object target, Type targetType, GsonBuilder builder) {
        if (target == null)
            return EMPTY_JSON;
        Gson gson = null;
        if (builder == null) {
            gson = new Gson();
        } else {
            gson = builder.create();
        }
        String result = EMPTY_JSON;
        try {
            if (targetType == null) {
                result = gson.toJson(target);
            } else {
                result = gson.toJson(target, targetType);
            }
        } catch (Exception ex) {
            if (target instanceof Collection<?> || target instanceof Iterator<?> || target instanceof Enumeration<?>
                    || target.getClass().isArray()) {
                result = EMPTY_JSON_ARRAY;
            }
        }
        return result;
    }
}