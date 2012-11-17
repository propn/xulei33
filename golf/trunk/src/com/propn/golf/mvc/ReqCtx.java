/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thunder.hsu
 * 
 */
public class ReqCtx {

    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);

    private static InheritableThreadLocal<Map<String, Object>> ctx = new InheritableThreadLocal<Map<String, Object>>();
    private static InheritableThreadLocal<Map<String, MultMap<String, Object>>> paramCtx = new InheritableThreadLocal<Map<String, MultMap<String, Object>>>();

    // @Context
    private static Map<String, Object> getContext() {
        Map<String, Object> context = ctx.get();
        if (null == context) {
            context = Collections.synchronizedMap(new HashMap<String, Object>());
            ctx.set(context);
        }
        return context;
    }

    public static Object getContext(String obj) {
        return getContext().get(obj);
    }

    // @PathParam
    private static MultMap<String, Object> getPathParam() {
        MultMap<String, Object> pathParam = paramCtx.get().get("PathParam");
        if (null == pathParam) {
            pathParam = new MultMap<String, Object>();
            paramCtx.get().put("PathParam", pathParam);
        }
        return pathParam;
    }

    public static List getPathParam(String parmName) {
        return getPathParam().get(parmName);
    }

    // @QueryParam
    private static MultMap<String, Object> getQueryParam() {
        MultMap<String, Object> queryParam = paramCtx.get().get("QueryParam");
        if (null == queryParam) {
            queryParam = new MultMap<String, Object>();
            paramCtx.get().put("QueryParam", queryParam);
        }
        return queryParam;
    }

    public static List getQueryParam(String parmName) {
        return getQueryParam().get(parmName);
    }

    // @FormParam
    private static MultMap<String, Object> getFormParam() {
        MultMap<String, Object> formParam = paramCtx.get().get("FormParam");
        if (null == formParam) {
            formParam = new MultMap<String, Object>();
            paramCtx.get().put("FormParam", formParam);
        }
        return formParam;
    }

    public static List getFormParam(String parmName) {
        return getFormParam().get(parmName);
    }

    // @HeaderParam
    private static MultMap<String, Object> getHeaderParam() {
        MultMap<String, Object> headerParam = paramCtx.get().get("HeaderParam");
        if (null == headerParam) {
            headerParam = new MultMap<String, Object>();
            paramCtx.get().put("HeaderParam", headerParam);
        }
        return headerParam;
    }

    public static List getHeaderParam(String parmName) {
        return getHeaderParam().get(parmName);
    }

    // @CookieParam
    private static MultMap<String, Object> getCookieParam() {
        MultMap<String, Object> cookieParam = paramCtx.get().get("CookieParam");
        if (null == cookieParam) {
            cookieParam = new MultMap<String, Object>();
            paramCtx.get().put("CookieParam", cookieParam);
        }
        return cookieParam;
    }

    public static List getCookieParam(String parmName) {
        return getCookieParam().get(parmName);
    }

    public static void init(HttpServletRequest request, HttpServletResponse response, Resource res) throws IOException {
        long start = System.currentTimeMillis();
        // @Context
        Map<String, Object> context = getContext();
        context.put("HttpServletRequest", request);
        context.put("HttpServletResponse", response);
        context.put("ServletInputStream", request.getInputStream());
        context.put("Cookie[]", request.getCookies());

        Map<String, MultMap<String, Object>> param = paramCtx.get();
        if (null == param) {
            param = new HashMap<String, MultMap<String, Object>>();
            paramCtx.set(param);
        }
        // @QueryParam
        initQueryParam(request.getQueryString());
        // @HeaderParam
        initHeaderParam(request);
        // @CookieParam
        initCookieParam(request);
        // @PathParam
        initPathParam(request.getServletPath(), res.getPath());
        // @FormParam
        initFormParam(request);

        log.debug("init ReqCtx cost time(millis):" + String.valueOf(System.currentTimeMillis() - start));
    }

    // TODO:
    private static void initFormParam(HttpServletRequest request) {
    }

    private static void initQueryParam(String queryString) {
        if (null == queryString) {
            return;
        }
        MultMap<String, Object> mMap = getQueryParam();
        String[] parms = queryString.split("&");
        for (String parm : parms) {
            String[] p = parm.split("=");
            mMap.put(p[0], p[1]);
            log.debug("QueryParam [" + p[0] + "=" + p[1] + "]");
        }
    }

    private static void initHeaderParam(HttpServletRequest request) {
        MultMap<String, Object> headerParam = getHeaderParam();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            Enumeration headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                Object headerValue = headers.nextElement();
                headerParam.put(headerName, headerValue);
                log.debug("HeaderParam [" + headerName + "=" + headerValue + "]");
            }
        }
    }

    private static void initCookieParam(HttpServletRequest request) {
        MultMap<String, Object> cookieParam = getCookieParam();
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return;
        }
        for (Cookie cookie : cookies) {
            cookieParam.put(cookie.getName(), cookie.getValue());
            log.debug("CookieParam [" + cookie.getName() + "=" + cookie.getValue() + "]");
        }
    }

    private static void initPathParam(String servletPath, String path) {
        if (servletPath.equals(path)) {
            return;
        }
        MultMap<String, Object> pathParam = getPathParam();
        String[] params = path.split("/");
        String[] paths = servletPath.split("/");
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (param.startsWith("{") && param.endsWith("}")) {
                param = param.substring(1, param.length() - 1);
                pathParam.put(param, paths[i]);
                log.debug("PathParam [" + param + "=" + paths[i] + "]");
            }
        }
    }
}
