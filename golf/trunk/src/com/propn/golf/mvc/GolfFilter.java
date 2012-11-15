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

import com.propn.golf.dao.trans.Atom;
import com.propn.golf.tools.JsonUtils;

public class GolfFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);
    private static final String IGNORE = "^(.+[.])(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|html)$";
    private static Pattern ignorePtn;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String regex = filterConfig.getInitParameter("ignore");
        if (!"null".equalsIgnoreCase(regex)) {
            ignorePtn = Pattern.compile(IGNORE, Pattern.CASE_INSENSITIVE);
        }
        String packages = filterConfig.getInitParameter("packages");
        try {
            if ("null".equalsIgnoreCase(regex)) {
                ResUtils.init("com", "org");
            } else {
                ResUtils.init(packages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }

    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (ignorePtn.matcher(request.getServletPath()).matches()) {
            chain.doFilter(request, response);
            return;
        }
        service(request, response, request.getServletPath());
    }

    /**
     * Dispatch client requests to a resource class.
     * 
     * @param baseUri the base URI of the request.
     * @param requestUri the URI of the request.
     * @param request the {@link HttpServletRequest} object that contains the request the client made to the Web
     *            component.
     * @param response the {@link HttpServletResponse} object that contains the response the Web component returns to
     *            the client.
     * @return the status code of the response.
     * @exception IOException if an input or output error occurs while the Web component is handling the HTTP request.
     * @exception ServletException if the HTTP request cannot be handled.
     */
    public void service(final HttpServletRequest request, HttpServletResponse response, final String servletPath)
            throws ServletException, IOException {
        Resource res = ResUtils.getMatchedRes(servletPath);

        if (null == res) {
            // HTTP 401 (Unauthorized) "Authorization Required"
            response.setStatus(404);
            response.setContentType("text/plain");
            response.getWriter().append("Not Found").flush();
            return;
        }
        if (!validate(request, response, res)) {
            // 405 Method Not Allowed
            // 415 Unsupported Media Type
            return;
        }

        String accept = request.getHeader("Accept");
        String[] produces = res.getProduces();
        String resptype = getOptimalType(accept, produces);
        if (null == resptype) {
            // 406 Not Acceptable Content-Type
            response.setStatus(406);
            response.setContentType("text/plain");
            response.getWriter().append("Not Acceptable Content-Type").flush();
            return;
        }
        try {
            Atom atom = new Atom(request, response);
            FutureTask<Object> transMgr = new FutureTask<Object>(atom);
            new Thread(transMgr).start();
            Object rst = transMgr.get();
            if (null == rst) {
                // 204 No Content
                response.setStatus(204);
            } else {
                response.setStatus(200);
                response.setContentType(resptype);
                response.getWriter().append(JsonUtils.toJson(rst)).flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validate(final HttpServletRequest request, HttpServletResponse response, Resource res)
            throws IOException {
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
            }
        }
        return true;
    }

    private String getOptimalType(String accept, String[] produces) {
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
