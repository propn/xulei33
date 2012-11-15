package com.propn.golf.dao.trans;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.propn.golf.dao.ds.ConnUtils;
import com.propn.golf.mvc.ReqCtx;
import com.propn.golf.mvc.Resource;

public class Atom implements Callable<Object> {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Resource res;

    public Atom(HttpServletRequest request, HttpServletResponse response, Resource res) {
        this.request = request;
        this.response = response;
        this.res = res;
    }

    @Override
    public Integer call() throws Exception {
        try {
            ReqCtx.init(request, response, res);
            ConnUtils.setTransId("1"); // 初始化上下文事务
            ConnUtils.commit();
        } catch (Exception e) {
            ConnUtils.rollbackAll();
        } finally {
            // ConnUtils.clean();
        }
        return 200;
    }
}