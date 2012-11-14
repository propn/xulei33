/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;

import com.propn.golf.dao.sql.RefUtils;
import com.propn.golf.tools.Cache;
import com.propn.golf.tools.ClassScaner;

/**
 * @author Administrator
 * 
 */
public class ResUtils {
    private static Cache<Resource> resCache = new Cache<Resource>();

    public static void init(String... packages) throws Exception {
        registerResouce(packages);
    }

    private static void registerResouce(String... packages) throws Exception {
        String PATH_VARIABLE_REXP = "[/]\\{\\S+\\}";
        Set<Class<?>> calssList = getPackageAllClasses(packages);
        for (Class<?> clz : calssList) {
            if (clz.isAnnotationPresent(Path.class)) {
                String basePath = ((Path) clz.getAnnotation(Path.class)).value();
                Map<String, Method> methodsMap = RefUtils.getMethods(clz);
                for (Iterator<String> it = methodsMap.keySet().iterator(); it.hasNext();) {
                    Method method = methodsMap.get(it.next());
                    if (method.isAnnotationPresent(Path.class)) {
                        String path = basePath + ((Path) method.getAnnotation(Path.class)).value();
                        Resource res = new Resource();
                        res.setPath(basePath);
                        res.setClassName(clz.getName());
                        res.setClazz(clz);
                        res.setMethodName(method.getName());
                        res.setMethod(method);
                        String compiledPath = path.replaceAll(PATH_VARIABLE_REXP, "/?");
                        if (null == resCache.get("path", compiledPath)) {
                            resCache.put("path", compiledPath, res);
                        } else {
                            res = resCache.get("path", compiledPath);
                            throw new Exception("URL[" + path + "]multiple registrations![" + clz.getName() + "."
                                    + method.getName() + "," + res.getClassName() + "." + res.getMethodName() + "]");
                        }
                    }
                }
            }
        }
    }

    private static Set<Class<?>> getPackageAllClasses(String... packages) throws IOException, ClassNotFoundException {
        Set<Class<?>> calssList = new LinkedHashSet<Class<?>>();
        List<String> classFilters = new ArrayList<String>();
        ClassScaner handler = new ClassScaner(true, true, classFilters);
        for (String pkg : packages) {
            calssList.addAll(handler.getPackageAllClasses(pkg, true));
        }
        return calssList;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            init("com.propn.mvc");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
