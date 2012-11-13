package com.propn.mvc;

import java.io.IOException;
import java.net.URI;
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

import com.propn.dao.trans.Atom;

public class WebContainer implements Filter {

    private static final String IGNORE = "^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf)$";
    private Pattern ignorePtn;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // compile ignore Regular expression
        String regex = filterConfig.getInitParameter("ignore");
        if (!"null".equalsIgnoreCase(regex)) {
            ignorePtn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        // scan&register resources
    }

    @Override
    public void destroy() {
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
        doFilter(request, response, chain, request.getRequestURI(), request.getServletPath(), request.getQueryString());
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            String requestURI, String servletPath, String queryString) throws IOException, ServletException {

        if (ignorePtn != null && ignorePtn.matcher(servletPath).matches()) {
            chain.doFilter(request, response);
            return;
        }

        final String requestURL = request.getRequestURL().toString();
        final URI baseUri = URI.create(request.getRequestURL().substring(0,
                requestURL.length() - servletPath.length() + 1));
        final URI requestUri = URI.create(requestURL);

        service(baseUri, requestUri, request, response);

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
    public int service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Atom res = new Atom(baseUri, requestUri, request, response);
            FutureTask<Object> resMgr = new FutureTask<Object>(res);
            new Thread(resMgr).start();
            Object rst;
            rst = resMgr.get();
            System.out.println("请求返回" + rst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 200;
    }

}
