package com.propn.golf.mvc;

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

import com.propn.golf.Golf;
import com.propn.golf.tools.StringUtils;

/**
 * 
 * 
 * @author Thunder.Hsu
 * @CreateDate 2012-11-17
 */
public class GolfFilter extends Golf implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);
    private static final String IGNORE = "^(.+[.])(jsp|png|gif|jpg|ttf|woff|eot|svg|js|css|jspx|jpeg|swf|html)$";

    private static Pattern ignorePattern;

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
        String[] pkgs = null;
        if (null == packages) {
            pkgs = getPkgs();
            log.debug("Packages : [{}]", StringUtils.array2Strig(pkgs, ","));
        } else {
            pkgs = packages.split(";");
            log.debug("Packages : " + packages);
        }
        try {
            init(pkgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
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
        String encoding = request.getCharacterEncoding();
        if (null != encoding && !encoding.equals("UTF-8")) {
            // 405 Method Not Allowed
            response.setStatus(400);
            response.setContentType(MediaType.TEXT_PLAIN);
            response.setHeader("Allow", "UTF-8");
            response.getWriter().append("CharacterEncoding Not Allowed!").flush();// Allow
            response.getWriter().close();
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

        String consumes = res.getConsumes();
        String contentType = request.getContentType();
        if (null != contentType && !consumes.isEmpty()) {
            contentType = contentType.split(";")[0];
            if (!consumes.contains(contentType)) {
                // 415 Unsupported Media Type
                response.setStatus(415);
                response.setContentType(MediaType.TEXT_PLAIN);
                response.setHeader("Support", consumes);
                response.getWriter().append("Unsupported Media Type!").flush();// Support
                return false;
            }
        }
        return true;
    }

    private String getOptimalType(String accept, String[] produces) {
        if (null == produces || produces.length == 0) {
            return MediaType.APPLICATION_JSON;
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

}
