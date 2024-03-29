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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.tools.Cache;
import com.propn.golf.tools.ClassUtils;
import com.propn.golf.tools.RefUtils;
import com.propn.golf.tools.StringUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class ResUtils {

    private static final Logger log = LoggerFactory.getLogger(ResUtils.class);
    private static Cache<Resource> resCache = new Cache<Resource>();
    private static final String PATH_PARAM_REXP = "/?\\{(\\S*?)\\}";// 匹配 /{a}

    public static void init(String... pkgs) throws Exception {
        registRes(pkgs);
    }

    public static Resource getMatchedRes(String path) {
        Resource res = getRes(path);
        if (null != res) {
            return res;
        } else {
            String[] paths = path.split("/");
            for (int i = paths.length - 1; i > 1; i--) {
                StringBuffer temp = new StringBuffer();
                for (int j = 1; j < i; j++) {
                    temp.append("/").append(paths[j]);
                }
                for (int j = i; j < paths.length - 1; j++) {
                    temp.append("/?");
                }
                temp.append("/?");
                log.debug("Search: " + temp);
                res = getRes(temp.toString());
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

    private static void registRes(String... packages) throws Exception {
        Set<Class<?>> calssList = getPackageAllClasses(packages);
        for (Class<?> clz : calssList) {
            registerRes(clz);
        }
    }

    public static void registerRes(Class<?> clz) throws Exception {
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

    private static Resource buildRes(Class<?> clz, Method method) {
        Resource res = new Resource();
        res.setClassName(clz.getName());
        res.setClz(clz);
        res.setMethodName(method.getName());
        res.setMethod(method);
        String path = ((Path) clz.getAnnotation(Path.class)).value() + method.getAnnotation(Path.class).value();
        String compiledPath = path.replaceAll(PATH_PARAM_REXP, "/?");
        res.setPath(path);
        res.setCompiledPath(compiledPath);

        String[] consumes = null;
        if (method.isAnnotationPresent(Consumes.class)) {
            consumes = method.getAnnotation(Consumes.class).value();
        }
        if (null == consumes && clz.isAnnotationPresent(Consumes.class)) {
            consumes = ((Consumes) clz.getAnnotation(Consumes.class)).value();
        }
        res.setConsumes(StringUtils.array2Strig(consumes, "|"));

        String[] produces = null;
        if (method.isAnnotationPresent(Produces.class)) {
            produces = method.getAnnotation(Produces.class).value();
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
        if (method.isAnnotationPresent(OPTIONS.class)) {
            httpMethods.append("OPTIONS|");
        }
        if (method.isAnnotationPresent(DELETE.class)) {
            httpMethods.append("DELETE|");
        }
        if (method.isAnnotationPresent(HEAD.class)) {
            httpMethods.append("HEAD|");
        }
        res.setHttpMethod(httpMethods.toString());

        return res;
    }

    private static Set<Class<?>> getPackageAllClasses(String... packages) throws IOException, ClassNotFoundException {
        Set<Class<?>> calssList = new LinkedHashSet<Class<?>>();
        List<String> classFilters = new ArrayList<String>();
        ClassUtils handler = new ClassUtils(true, true, classFilters);
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
            // init("com.propn.golf.mvc");
            String PATH_VARIABLE_REXP = "/?\\{(\\S*?)\\}";
            String path = "/version/get2/{pathv}/{abc}/{def}";
            String compiledPath = path.replaceAll(PATH_VARIABLE_REXP, "/?");

            // /version/get2/?/?/?
            System.out.println(compiledPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
