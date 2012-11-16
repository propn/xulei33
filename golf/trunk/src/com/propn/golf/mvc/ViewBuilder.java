/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.propn.golf.tools.JsonUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class ViewBuilder implements Thread.UncaughtExceptionHandler {

    private static ThreadLocal<Throwable> error = new ThreadLocal<Throwable>();
    private static ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        error.set(e);
    }

    public void setResp(HttpServletResponse resp) {
        response.set(resp);
    }

    public static void build(String resptype, Object rst) throws IOException {
        Throwable e = error.get();
        HttpServletResponse resp = response.get();
        if (null == rst) {
            // 204 No Content
            resp.setStatus(204);
        } else {
            resp.setStatus(200);
            resp.setContentType(resptype);
            resp.getWriter().append(JsonUtils.toJson(rst)).flush();
        }
    }
}
