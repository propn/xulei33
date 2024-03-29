/**
 * 
 */
package com.propn.golf.mvc;

import java.lang.reflect.Method;

/**
 * @author Administrator
 * 
 */
public class Resource {
    private String httpMethod;// PUT, GET, POST , DELETE
    private String consumes;// Accept ContentType
    private String[] produces;// Return ContentType

    private String path;
    private String compiledPath;
    private String className;
    private Class<?> clz;
    private String methodName;
    private Method method;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompiledPath() {
        return compiledPath;
    }

    public void setCompiledPath(String compiledPath) {
        this.compiledPath = compiledPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
