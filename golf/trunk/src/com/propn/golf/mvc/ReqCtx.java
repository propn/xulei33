/**
 * 
 */
package com.propn.golf.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.propn.golf.tools.MultMap;
import com.propn.golf.tools.StringUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class ReqCtx {
    static final String charsetName = "UTF-8";
    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);

    private static InheritableThreadLocal<Map<String, Object>> ctx = new InheritableThreadLocal<Map<String, Object>>();
    private static InheritableThreadLocal<Map<String, MultMap<String, Object>>> paramCtx = new InheritableThreadLocal<Map<String, MultMap<String, Object>>>();

    public static void init(HttpServletRequest request, HttpServletResponse response, Resource res) throws IOException {
        long start = System.currentTimeMillis();
        Map<String, MultMap<String, Object>> param = paramCtx.get();
        if (null == param) {
            param = new HashMap<String, MultMap<String, Object>>();
            paramCtx.set(param);
        }
        // @Context
        initContext(request, response);
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
        // @Multipart/form-data
        initMultipart(request, response);
        log.debug("init ReqCtx cost time(millis):" + String.valueOf(System.currentTimeMillis() - start));
    }

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

    private static void initContext(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> context = getContext();
        context.put("HttpServletRequest", request);
        context.put("HttpServletResponse", response);
        if (request.getContentType().contains(MediaType.MULTIPART_FORM_DATA)) {
            context.put("InputStream", request.getInputStream());
        } else {
            // 非文件上传,则转换为ByteArrayInputStream
            ByteArrayInputStream bin = StringUtils.servletInputStream2ByteArrayInputStream(request.getInputStream());
            context.put("InputStream", bin);
        }
        context.put("Cookie[]", request.getCookies());
    }

    private static void initPathParam(String servletPath, String path) throws UnsupportedEncodingException {
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
                String v = URLDecoder.decode(paths[i], charsetName);
                pathParam.put(param, v);
                log.debug("PathParam [" + param + "=" + v + "]");
            }
        }
    }

    private static void initQueryParam(String queryString) throws UnsupportedEncodingException {
        if (null == queryString) {
            return;
        }
        MultMap<String, Object> mMap = getQueryParam();
        String[] parms = queryString.split("&");
        for (String parm : parms) {
            String[] pv = parm.split("=");
            String p = URLDecoder.decode(pv[0], charsetName);
            String v = URLDecoder.decode(pv[1], charsetName);
            mMap.put(p, v);
            log.debug("QueryParam [" + p + "=" + v + "]");
        }
    }

    private static void initFormParam(HttpServletRequest request) throws IOException {
        MultMap<String, Object> mMap = getFormParam();
        if (null != request.getContentType() && MediaType.APPLICATION_FORM_URLENCODED.equals(request.getContentType())) {
            ByteArrayInputStream bin = (ByteArrayInputStream) getContext("InputStream");
            byte[] b = new byte[bin.available()];
            bin.read(b, 0, bin.available());
            String entity = new String(b, charsetName);
            final StringTokenizer tokenizer = new StringTokenizer(entity, "&");
            String token;
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                int idx = token.indexOf('=');
                if (idx < 0) {
                    String param = URLDecoder.decode(token, charsetName);
                    mMap.put(URLDecoder.decode(token, charsetName), null);
                    log.debug("FormParam [" + param + "=" + null + "]");
                } else if (idx > 0) {
                    String param = URLDecoder.decode(token.substring(0, idx), charsetName);
                    String v = URLDecoder.decode(token.substring(idx + 1), charsetName);
                    mMap.put(param, v);
                    log.debug("FormParam [" + param + "=" + v + "]");
                }
            }
        }
    }

    private static void initHeaderParam(HttpServletRequest request) throws UnsupportedEncodingException {
        MultMap<String, Object> headerParam = getHeaderParam();
        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            // headerName = URLDecoder.decode(headerName, charsetName);
            Enumeration<?> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = (String) headers.nextElement();
                // headerValue = URLDecoder.decode(headerValue, charsetName);
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

    private static void initMultipart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getContentType().contains(MediaType.MULTIPART_FORM_DATA)) {

            if (request.getContentLength() > 10487600) {
                // 413 Request Entity Too Large
                response.setCharacterEncoding("UTF-8");
                response.setStatus(413);
                PrintWriter out = response.getWriter();
                out.print("Request Entity Too Large,Max Upload 10M!");
                out.flush();
                out.close();
                throw new RuntimeException("Request Entity Too Large,Max Upload 10M!");
            }

            MultipartParser mp = new MultipartParser(request, 10487600);// 1024 * 1024 * 10
            mp.setEncoding("utf-8");
            Part part;
            List<FileInfo> fileParts = new ArrayList<FileInfo>();
            while ((part = mp.readNextPart()) != null) {
                String name = part.getName();
                if (part.isParam()) {
                    ParamPart pp = (ParamPart) part;
                    String value = pp.getStringValue();
                    getFormParam().put(name, value);
                    log.debug("FormParam [" + name + "=" + value + "]");
                } else if (part.isFile()) {
                    FilePart fp = (FilePart) part;
                    if (null != fp.getFileName()) {
                        fileParts.add(fp);
                    }

                } else {
                    System.out.println("File name:" + name);
                }
            }

            FileInfo[] files = new FileInfo[fileParts.size()];
            int i = 0;
            for (FileInfo fileInfo : fileParts) {
                files[i] = fileInfo;
                i++;
            }
            getContext().put("FileInfo[]", files);
        }
    }
}
