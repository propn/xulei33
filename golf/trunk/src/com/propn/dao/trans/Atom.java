package com.propn.dao.trans;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.propn.dao.ds.ConnUtils;

public class Atom implements Callable<Object> {
    private URI baseUri;
    private URI requestUri;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public Atom() {
    }

    public Atom(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response) {
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.request = request;
        this.response = response;
    }

    @Override
    public Integer call() throws Exception {
        try {

            ConnUtils.setTransId("1"); // 初始化上下文事务
            // 业务处理
            // 1.doFilter
            // 2.doService
            // 3.buildResp
            ConnUtils.commit();
        } catch (Exception e) {
            ConnUtils.rollbackAll();
        } finally {
            ConnUtils.clean();
        }
        return 200;
    }
}