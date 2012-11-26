/**
 * 
 */
package com.propn.golf.ioc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.mvc.Golf;
import com.propn.golf.tools.ClassScaner;
import com.propn.golf.tools.RefUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    private static Map<String, Class<?>> beanMap = null;
    private static final Map<Class<?>, Object> instMap = Collections.synchronizedMap(new HashMap<Class<?>, Object>());

    public static <T> T getInstance(Class<? extends T> clazz) throws Exception {
        T instance = clazz.newInstance();
        instMap.put(clazz, instance);
        dependencyInjection(instance);
        return clazz.cast(instance);
    }

    synchronized public static void registBean(String... packages) throws IOException, ClassNotFoundException,
            Exception {
        if (null == beanMap) {
            beanMap = Collections.synchronizedMap(new HashMap<String, Class<?>>());
        } else {
            beanMap.clear();
        }
        List<String> classFilters = new ArrayList<String>();
        ClassScaner handler = new ClassScaner(true, true, classFilters);
        for (String pkg : packages) {
            Set<Class<?>> clazzs = handler.getPackageAllClasses(pkg, true);
            registBean(clazzs);
        }
    }

    public static void registBean(Set<Class<?>> clazzs) throws Exception {
        for (Class clazz : clazzs) {
            if (clazz.isAnnotationPresent(Named.class)) {
                registBean(clazz);
            }
        }
    }

    private static void dependencyInjection(Object obj) throws Exception {
        Set<Entry<String, Field>> fields = RefUtils.getFields(obj.getClass()).entrySet();
        for (Entry<String, Field> entry : fields) {
            Field field = entry.getValue();
            if (field.isAnnotationPresent(Inject.class)) {
                // 注入属性
                String fieldName = entry.getKey();
                Class clz = BeanUtils.getBean(fieldName, field.getType());
                if (null != clz) {
                    field.set(obj, getInstance(clz));
                } else {
                    StringBuffer msgb = new StringBuffer(obj.getClass().getName());
                    msgb.append(" 属性[");
                    msgb.append(fieldName);
                    msgb.append("]无适合Bean初始化!");
                    String msg = msgb.toString();
                    log.error(msg);
                    throw new Exception();
                }
            }
        }
    }

    private static <T> Class<?> getBean(String beanName, Class<? extends T> clazz) throws Exception {
        if (null == beanMap) {
            registBean(Golf.getPkgs());
        }
        Class<?> clz = beanMap.get(beanName);
        if (null == clz) {
            return null;
        }
        if (clz == clazz) {
            return clz;
        } else {
            if (clazz.isAssignableFrom(clz)) {
                return clz;
            } else {
                StringBuffer msgb = new StringBuffer("bean[");
                msgb.append(beanName);
                msgb.append("]注册类[");
                msgb.append(clz);
                msgb.append("]与属性类[");
                msgb.append(beanMap.get(beanName));
                msgb.append("]不匹配!");
                String msg = msgb.toString();
                log.error(msg);
                throw new Exception(msg);
            }
        }
    }

    private static <T> void registBean(Class<? extends T> clz) throws Exception {
        Named name = clz.getAnnotation(Named.class);
        String beanName = name.value();
        if (null == beanName || "".equals(beanName)) {
            String clssName = clz.getSimpleName();
            beanName = clssName.substring(0, 1).toLowerCase() + clssName.substring(1);
        }
        if (null != beanMap.get(beanName)) {
            StringBuffer msgb = new StringBuffer("bean[");
            msgb.append(beanName);
            msgb.append("]重复注册![");
            msgb.append(clz);
            msgb.append(",");
            msgb.append(beanMap.get(beanName));
            msgb.append("]");
            String msg = msgb.toString();
            log.error(msg);
            throw new Exception(msg);
        }
        beanMap.put(beanName, clz);
    }

}
