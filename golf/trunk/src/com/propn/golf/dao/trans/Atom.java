package com.propn.golf.dao.trans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.propn.golf.mvc.ReqCtx;
import com.propn.golf.mvc.Resource;
import com.propn.golf.tools.BeanFactory;

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
    public Object call() {
        Object rst = null;
        try {
            ReqCtx.init(request, response, res);
            // ConnUtils.setTransId("1"); // 初始化上下文事务
            rst = invoke(res);
            // ConnUtils.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // ConnUtils.rollbackAll();
            return e;
        } finally {
            // ConnUtils.clean();
        }
        return rst;
    }

    private Object invoke(Resource res) throws Exception {
        Class<?> clz = res.getClz();
        Method method = res.getMethod();
        Object obj = BeanFactory.getInstance(clz);

        Class[] paramTypes = method.getParameterTypes();
        for (Class clazz : paramTypes) {
            System.out.println(clazz.getName());
        }
        //
        Annotation[][] a = method.getParameterAnnotations();
        for (int i = 0; i < a.length; i++) {
            Annotation[] annotations = a[i];
            Annotation annotation = annotations[0];
            System.out.println(annotation.annotationType().getSimpleName());
            System.out.println(annotation.getClass());
        }

        Object rst = null;
        Object[] params = new Object[] {};
        // rst = method.invoke(obj, params);
        return rst;
    }
}