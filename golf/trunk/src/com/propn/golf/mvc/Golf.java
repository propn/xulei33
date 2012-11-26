package com.propn.golf.mvc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Thunder.Hsu
 * @CreateDate 2012-11-17
 */
public class Golf implements Filter {

    private static final Logger log = LoggerFactory.getLogger(Golf.class);
    private static final String IGNORE = "^(.+[.])(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|html)$";

    private static Pattern ignorePattern;
    private static String appPath = null;
    private static String packages[] = null;

    public static String getAppPath() {
        return appPath;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        long begin = System.currentTimeMillis();
        log.info("golf begin start.");
        appPath = filterConfig.getServletContext().getRealPath("/");
        log.info("Path [{}] ", appPath);

        String regex = filterConfig.getInitParameter("ignore");
        if (null == regex) {
            ignorePattern = Pattern.compile(IGNORE, Pattern.CASE_INSENSITIVE);
            log.debug("ignorePattern : " + IGNORE);
        } else {
            ignorePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }

        String packages = filterConfig.getInitParameter("packages");
        try {
            if (null == packages) {
                String[] pkgs = getPkgs();
                ResUtils.init(pkgs);
                log.debug("Packages : [{}]", pkgs);
            } else {
                ResUtils.init(packages.split(","));
                log.debug("Packages : " + packages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("golf started. Time used(millis): " + String.valueOf(System.currentTimeMillis() - begin));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            request = (HttpServletRequest) request;
            response = (HttpServletResponse) response;
        } catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String servletPath = request.getServletPath();

        if (ignorePattern.matcher(servletPath).matches()) {
            chain.doFilter(request, response);
            return;
        }

        Resource res = ResUtils.getMatchedRes(servletPath);
        if (!validate(request, response, res)) {
            // HTTP 404 Not Found
            // 406 Not Acceptable Content-Type
            // 405 Method Not Allowed
            // 415 Unsupported Media Type
            return;
        }

        String accept = request.getHeader("Accept");
        String[] produces = res.getProduces();
        String mediaType = getOptimalType(accept, produces);
        if (null == mediaType) {
            // 406 Not Acceptable Content-Type
            response.setStatus(406);
            response.setContentType("text/plain");
            response.getWriter().append("Not Acceptable Content-Type").flush();
            return;
        }
        call(request, response, res, mediaType);
    }

    private void call(HttpServletRequest request, HttpServletResponse response, Resource res, String mediaType)
            throws IOException, ServletException {
        Atom atom = new Atom(request, response, res);
        FutureTask<Object> transMgr = new FutureTask<Object>(atom);
        new Thread(transMgr).start();
        Object rst = null;
        try {
            rst = transMgr.get();
        } catch (Exception e) {
            // FutureTask
            throw new RuntimeException(e);
        }
        ViewBuilder.build(request, response, mediaType, rst);
    }

    private boolean validate(final HttpServletRequest request, HttpServletResponse response, Resource res)
            throws IOException {

        if (null == res) {
            // HTTP 404 Not Found
            response.setStatus(404);
            response.setContentType("text/plain");
            response.getWriter().append("Not Found").flush();
            return false;
        }

        String acceptHttpMethods = res.getHttpMethod();
        if (!acceptHttpMethods.contains(request.getMethod())) {
            // 405 Method Not Allowed
            response.setStatus(405);
            response.setContentType(MediaType.TEXT_PLAIN);
            response.setHeader("Allow", acceptHttpMethods);
            response.getWriter().append("Method Not Allowed!").flush();// Allow
            response.getWriter().close();
            return false;
        }

        String[] consumes = res.getConsumes();
        String contentType = request.getContentType();
        if (null != contentType && consumes.length > 0) {
            StringBuffer temp = new StringBuffer();
            for (String consume : consumes) {
                temp.append(consume).append(",");
            }
            if (!temp.toString().contains(contentType)) {
                // 415 Unsupported Media Type
                response.setStatus(415);
                response.setContentType(MediaType.TEXT_PLAIN);
                response.setHeader("Support", temp.toString());
                response.getWriter().append("Unsupported Media Type!").flush();// Support
                return false;
            }
        }
        return true;
    }

    private String getOptimalType(String accept, String[] produces) {
        if (null == produces || produces.length == 0) {
            return "application/xml";
        }
        if (accept.contains("*/*")) {
            return produces[0];
        }
        for (String type : produces) {
            if (accept.contains(type)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public void destroy() {

    }

    public static String[] getPkgs() {

        if (null != packages && packages.length != 0) {
            return packages;
        }

        String classPath = null;// classPath
        if (null != appPath) {
            classPath = appPath + "WEB-INF\\classes";
        } else {
            classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        }
        File dir = new File(classPath);
        File[] files = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            @Override
            public boolean accept(File file) {
                if (file.isDirectory() && !file.getName().equals("javax") && !file.getName().equals("java")) {
                    return true;
                }
                return false;
            }
        });

        String[] pkgs = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            pkgs[i] = files[i].getName();
        }
        // 设置
        packages = pkgs;
        return pkgs;
    }
}
