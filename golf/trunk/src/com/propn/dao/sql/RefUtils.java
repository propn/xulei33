package com.propn.dao.sql;

import java.lang.reflect.Field;
import java.util.Map;

import com.propn.dao.Person;
import com.propn.tools.Cache;

/**
 * 
 * @author Thunder.xu
 * 
 */
public class RefUtils {
    private static Cache<Field> cache;
    private static RefUtils inst;

    private RefUtils() {

    }

    public static RefUtils getInstance() {
        if (null == inst) {
            cache = new Cache<Field>();
            inst = new RefUtils();
            return inst;
        }
        return inst;
    }

    public Object getFieldValue(Object obj, String fieldName) throws Exception {
        return getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    public void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        setFieldValue(obj, getField(obj.getClass(), fieldName), value);
    }

    public Map<String, Field> getFields(Class clz) {
        String className = clz.getName();
        if (cache.get(className).isEmpty()) {
            cacheFields(clz);
        }
        return cache.get(className);
    }

    public Field getField(Class clz, String fieldName) throws Exception {
        Map<String, Field> fields = getFields(clz);
        Field o = fields.get(fieldName);
        if (null == o) {
            throw new Exception(clz.getName() + "不存在属性:" + fieldName);
        }
        return o;
    }

    private static void cacheFields(Class clz) {
        Class superClass = clz.getSuperclass();
        while (true) {
            if (superClass != null) {
                Field[] superFields = superClass.getDeclaredFields();
                if (superFields != null && superFields.length > 0) {
                    for (Field field : superFields) {
                        field.setAccessible(true);
                        cache.put(clz.getName(), field.getName(), field);
                    }
                }
                superClass = superClass.getSuperclass();
            } else {
                break;
            }
        }
        Field[] objFields = clz.getDeclaredFields();
        if (objFields != null && objFields.length > 0) {
            for (Field field : objFields) {
                field.setAccessible(true);
                cache.put(clz.getName(), field.getName(), field);
            }
        }
    }

    private static Object getFieldValue(Object obj, Field field) throws Exception {
        return field.get(obj);
    }

    private static void setFieldValue(Object obj, Field field, Object value) throws Exception {
        field.set(obj, value);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        RefUtils refUtils = RefUtils.getInstance();
        Person obj = new Person();
        try {
            System.out.println(refUtils.getFieldValue(obj, "personId"));
            System.out.println(refUtils.getFieldValue(obj, "personName"));
            System.out.println(refUtils.getFieldValue(obj, "age"));

            refUtils.setFieldValue(obj, "personId", "123");
            refUtils.setFieldValue(obj, "personName", "123");
            refUtils.setFieldValue(obj, "age", 123);

            System.out.println(refUtils.getFieldValue(obj, "personId"));
            System.out.println(refUtils.getFieldValue(obj, "personName"));
            System.out.println(refUtils.getFieldValue(obj, "age"));

        } catch (Exception e) {
        }

    }

}
