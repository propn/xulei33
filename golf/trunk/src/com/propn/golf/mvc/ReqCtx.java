/**
 * 
 */
package com.propn.golf.mvc;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Thunder.hsu
 * 
 */
public class ReqCtx {

    private static final InheritableThreadLocal<Map<String, MultMap>> reqCtx = new InheritableThreadLocal<Map<String, MultMap>>();

    // @PathParam
    public static MultMap getPathParam() {
        return reqCtx.get().get("QueryParam");
    }

    public static List getPathParam(String parmName) {
        return reqCtx.get().get("QueryParam").get(parmName);
    }

    // @QueryParam
    public static MultMap getQueryParam() {
        return reqCtx.get().get("QueryParam");
    }

    public static List getQueryParam(String parmName) {
        return reqCtx.get().get("QueryParam").get(parmName);
    }

    // @HeaderParam
    public static MultMap getHeaderParam() {
        return reqCtx.get().get("QueryParam");
    }

    public static List getHeaderParam(String parmName) {
        return reqCtx.get().get("QueryParam").get(parmName);
    }

    // @Context
    public static MultMap getContext() {
        return reqCtx.get().get("QueryParam");
    }

    public static List getContext(String parmName) {
        return reqCtx.get().get("QueryParam").get(parmName);
    }

    public static void init(HttpServletRequest request, String path) {
        String queryString = request.getQueryString();
        Enumeration parameterNames = request.getParameterNames();
        Cookie[] cookies = request.getCookies();

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}
