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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.dao.sql.RefUtils;
import com.propn.golf.tools.Cache;
import com.propn.golf.tools.ClassScaner;

/**
 * @author Administrator
 * 
 */
public class ResUtils {
    private static final Logger log = LoggerFactory.getLogger(ResUtils.class);
    private static Cache<Resource> resCache = new Cache<Resource>();
    private static final String PATH_VARIABLE_REXP = "[/]\\{\\S+\\}";

    public static void init(String... packages) throws Exception {
        registerResouce(packages);
    }

    public static Resource getMatchedRes(String path) {
        Resource res = getRes(path);
        if (null != res) {
            return res;
        } else {
            String[] paths = path.split("/");
            for (int i = paths.length; i > 1; i--) {
                String temp = "";
                for (int j = 1; j < i; j++) {
                    temp = temp + "/" + paths[j];
                }
                for (int j = i; j < paths.length; j++) {
                    temp = temp + "/?";
                }
                res = getRes(path);
                if (null != res) {
                    break;
                }
            }
        }
        return res;
    }

    private static Resource getRes(String path) {
        return resCache.get("path", path);
    }

    private static void registerResouce(String... packages) throws Exception {
        Set<Class<?>> calssList = getPackageAllClasses(packages);
        for (Class<?> clz : calssList) {
            if (clz.isAnnotationPresent(Path.class)) {
                Map<String, Method> methodsMap = RefUtils.getMethods(clz);
                for (Iterator<String> it = methodsMap.keySet().iterator(); it.hasNext();) {
                    Method method = methodsMap.get(it.next());
                    if (method.isAnnotationPresent(Path.class)) {
                        Resource res = buildRes(clz, method);
                        if (null == resCache.get("path", res.getCompiledPath())) {
                            resCache.put("path", res.getCompiledPath(), res);
                            StringBuffer info = new StringBuffer("URL[");
                            info.append(res.getCompiledPath());
                            info.append("] register to [");
                            info.append(res.getClassName());
                            info.append(".");
                            info.append(res.getMethodName());
                            info.append("]");
                            log.debug(info.toString());
                        } else {
                            res = resCache.get("path", res.getCompiledPath());
                            StringBuffer error = new StringBuffer("URL[");
                            error.append(res.getCompiledPath());
                            error.append("]multiple registrations![");
                            error.append(clz.getName());
                            error.append(".");
                            error.append(method.getName());
                            error.append(",");
                            error.append(res.getClassName());
                            error.append(".");
                            error.append(res.getMethodName()).append("]");
                            String errMsg = error.toString();
                            log.error(errMsg);
                            throw new RuntimeException(errMsg);
                        }
                    }
                }
            }
        }
    }

    private static Resource buildRes(Class clz, Method method) {
        Resource res = new Resource();
        res.setClassName(clz.getName());
        res.setClazz(clz);
        res.setMethodName(method.getName());
        res.setMethod(method);
        String path = ((Path) clz.getAnnotation(Path.class)).value()
                + ((Path) method.getAnnotation(Path.class)).value();
        String compiledPath = path.replaceAll(PATH_VARIABLE_REXP, "/?");
        res.setPath(path);
        res.setCompiledPath(compiledPath);

        String[] consumes = null;
        if (method.isAnnotationPresent(Consumes.class)) {
            consumes = ((Consumes) method.getAnnotation(Consumes.class)).value();
        }
        if (null == consumes && clz.isAnnotationPresent(Consumes.class)) {
            consumes = ((Consumes) clz.getAnnotation(Consumes.class)).value();
        }
        res.setConsumes(consumes);
        String[] produces = null;
        if (method.isAnnotationPresent(Produces.class)) {
            produces = ((Produces) method.getAnnotation(Produces.class)).value();
        }
        if (null == produces && clz.isAnnotationPresent(Produces.class)) {
            produces = ((Produces) clz.getAnnotation(Produces.class)).value();
        }
        res.setProduces(produces);
        // httpMethods
        StringBuffer httpMethods = new StringBuffer();
        if (method.isAnnotationPresent(GET.class)) {
            httpMethods.append("GET|");
        }
        if (method.isAnnotationPresent(POST.class)) {
            httpMethods.append("POST|");
        }
        res.setHttpMethod(httpMethods.toString());
        return res;
    }

    private static Set<Class<?>> getPackageAllClasses(String... packages) throws IOException, ClassNotFoundException {
        Set<Class<?>> calssList = new LinkedHashSet<Class<?>>();
        List<String> classFilters = new ArrayList<String>();
        ClassScaner handler = new ClassScaner(true, true, classFilters);
        if (packages.length == 0) {
            return handler.getPackageAllClasses(null, true);
        }
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
            init("com.propn.golf.mvc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
