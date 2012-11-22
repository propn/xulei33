package com.propn.golf.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
import com.propn.golf.tools.StringUtils;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

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
        for (int i = 0; i < argsClass.length; i++) {
            Class temp = argsClass[i];
            if (temp.equals(ServletRequest.class)) {
                args[i] = ReqCtx.getContext("HttpServletRequest");
                continue;
            }
            if (temp.equals(ServletResponse.class)) {
                args[i] = ReqCtx.getContext("HttpServletResponse");
                continue;
            }
            if (temp.equals(ServletInputStream.class)) {
                args[i] = ReqCtx.getContext("ServletInputStream");
                continue;
            }
            if (temp.equals(Cookie[].class)) {
                args[i] = ReqCtx.getContext("Cookie[]");
                continue;
            }
            // 其他Java对象绑定
        }

        Annotation[][] a = method.getParameterAnnotations();
        for (int i = 0; i < a.length; i++) {
            if (null != args[i]) {// initiative with ParameterType
                continue;
            }
            Annotation[] annotations = a[i];
            if (null == annotations || annotations.length == 0) {
                throw new Exception(method.getName() + "'s params[" + i + "] init error!no Annotation!");
            }
            Annotation annotation = annotations[0];
            // @PathParam
            if (annotation.annotationType().equals(PathParam.class)) {
                PathParam p = (PathParam) annotation;
                args[i] = ReqCtx.getPathParam(p.value()).get(0);
                continue;
            }
            // @QueryParam
            if (annotation.annotationType().equals(QueryParam.class)) {
                QueryParam p = (QueryParam) annotation;
                if (argsClass[i].equals(List.class)) {
                    args[i] = ReqCtx.getQueryParam(p.value());
                } else {
                    args[i] = StringUtils.list2Stirng(ReqCtx.getQueryParam(p.value()));
                }
                continue;
            }

            // @FormParam
            if (annotation.annotationType().equals(FormParam.class)) {
                FormParam p = (FormParam) annotation;
                if (argsClass[i].equals(List.class)) {
                    args[i] = ReqCtx.getQueryParam(p.value());
                } else {
                    args[i] = StringUtils.list2Stirng(ReqCtx.getQueryParam(p.value()));
                }
                continue;
            }

            // @HeaderParam
            if (annotation.annotationType().equals(HeaderParam.class)) {
                HeaderParam p = (HeaderParam) annotation;
                if (null != ReqCtx.getHeaderParam(p.value())) {
                    args[i] = ReqCtx.getHeaderParam(p.value()).get(0);
                }
                continue;
            }

            // @CookieParam
            if (annotation.annotationType().equals(CookieParam.class)) {
                CookieParam p = (CookieParam) annotation;
                if (null != ReqCtx.getCookieParam(p.value())) {
                    args[i] = ReqCtx.getCookieParam(p.value()).get(0);
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