package com.propn.golf.dao.trans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.propn.golf.dao.ds.ConnUtils;
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
            // 独立线程
            ReqCtx.init(request, response, res);
            ConnUtils.setTransId("1"); // 初始化上下文事务
            rst = invoke(res);
            ConnUtils.commit();
        } catch (Exception e) {
            ConnUtils.rollbackAll();
        } finally {
            // ConnUtils.clean();
        }
        return rst;
    }

    private Object invoke(Resource res) throws Exception {
        Class<?> clz = res.getClz();
        Object obj = BeanFactory.getInstance(clz);
        Method method = res.getMethod();

        Class[] paramTypeList = method.getParameterTypes();
        Class returnType = method.getReturnType();
        System.out.println(returnType);
        for (Class clazz : paramTypeList) {
            System.out.println(clazz);
        }
        Annotation[][] a = method.getParameterAnnotations();
        for (int i = 0; i < a.length; i++) {
            Annotation[] annotations = a[i];
            for (int j = 0; j < annotations.length; j++) {
                Annotation annotation = annotations[j];
                System.out.println(annotation.getClass());
            }
        }
        System.out.println(obj.getClass().getName());
        System.out.println(method.getName());
        Object rst = null;
        rst = method.invoke(obj, null);
        System.err.println(rst);
        return rst;
    }

}