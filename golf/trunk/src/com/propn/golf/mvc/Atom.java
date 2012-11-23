package com.propn.golf.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.dao.trans.Trans;
import com.propn.golf.tools.BeanFactory;
import com.propn.golf.tools.ConvertUtils;

public class Atom implements Callable<Object> {

    private static final Logger log = LoggerFactory.getLogger(Atom.class);

    private final HttpServletRequest request;
    private HttpServletResponse response;
    private final Resource res;

    public Atom(HttpServletRequest request, HttpServletResponse response, Resource res) {
        this.request = request;
        this.response = response;
        this.res = res;
    }

    @Override
    public Object call() {
        Object rst = null;
        try {
            ReqCtx.init(request, response, res);
            long t0 = System.currentTimeMillis();
            rst = invoke(res);
            log.debug("method call cost time (millis):" + String.valueOf(System.currentTimeMillis() - t0));
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        } finally {
        }
        return rst;
    }

    private Object invoke(final Resource res) throws Exception {
        long start = System.currentTimeMillis();
        Class<?> clz = res.getClz();
        final Object obj = BeanFactory.getInstance(clz);
        final Method method = res.getMethod();
        Class[] argsClass = method.getParameterTypes();
        if (argsClass.length == 0) {
            log.debug("init method call cost time (millis):" + String.valueOf(System.currentTimeMillis() - start));
            Object rst = Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    return method.invoke(obj, null);
                }
            });
            return rst;
        }
        // 动态构造参数
        final Object[] args = new Object[argsClass.length];// 函数入参

        // 按照参数类型进行绑定
        for (int i = 0; i < argsClass.length; i++) {
            Class temp = argsClass[i];
            if (temp.equals(HttpServletRequest.class)) {
                args[i] = ReqCtx.getContext("HttpServletRequest");
                continue;
            }
            if (temp.equals(HttpServletResponse.class)) {
                args[i] = ReqCtx.getContext("HttpServletResponse");
                continue;
            }
            if (temp.equals(ServletInputStream.class)) {
                args[i] = ReqCtx.getContext("InputStream");
                continue;
            }
            if (temp.equals(Cookie[].class)) {
                args[i] = ReqCtx.getContext("Cookie[]");
                continue;
            }
        }

        // 绑定基础对象参数PathParam/QueryParam/FormParam/HeaderParam/CookieParam
        Annotation[][] a = method.getParameterAnnotations();
        for (int i = 0; i < a.length; i++) {
            if (null != args[i]) {// 已经绑定
                continue;
            }
            Annotation[] annotations = a[i];
            if (null == annotations || annotations.length == 0) {
                if (argsClass[i].getPackage().equals("java.lang")) {
                    throw new Exception(method.getName() + "'s params[" + i + "] init error!no Annotation!");
                }
                continue;
            }
            Annotation annotation = annotations[0];
            // @PathParam
            if (annotation.annotationType().equals(PathParam.class)) {
                PathParam p = (PathParam) annotation;
                args[i] = ConvertUtils.convert(ReqCtx.getPathParam(p.value()).get(0), argsClass[i]);
                continue;
            }
            // @QueryParam
            if (annotation.annotationType().equals(QueryParam.class)) {
                QueryParam p = (QueryParam) annotation;
                args[i] = ConvertUtils.convert(ReqCtx.getQueryParam(p.value()), argsClass[i]);
                continue;
            }

            // @FormParam
            if (annotation.annotationType().equals(FormParam.class)) {
                FormParam p = (FormParam) annotation;
                args[i] = ConvertUtils.convert(ReqCtx.getFormParam(p.value()), argsClass[i]);
                continue;
            }

            // @HeaderParam
            if (annotation.annotationType().equals(HeaderParam.class)) {
                HeaderParam p = (HeaderParam) annotation;
                if (null != ReqCtx.getHeaderParam(p.value())) {
                    args[i] = ConvertUtils.convert(ReqCtx.getHeaderParam(p.value()).get(0), argsClass[i]);
                }
                continue;
            }
            // @CookieParam
            if (annotation.annotationType().equals(CookieParam.class)) {
                CookieParam p = (CookieParam) annotation;
                if (null != ReqCtx.getCookieParam(p.value())) {
                    args[i] = ConvertUtils.convert(ReqCtx.getCookieParam(p.value()).get(0), argsClass[i]);
                }
                continue;
            }
        }
        log.debug("init method call cost time (millis):" + String.valueOf(System.currentTimeMillis() - start));
        // 使用独立数据库事务调用服务方法!
        Object rst = Trans.transNew(new Trans() {
            @Override
            public Object call() throws Exception {
                return method.invoke(obj, args);
            }
        });
        return rst;
    }
}