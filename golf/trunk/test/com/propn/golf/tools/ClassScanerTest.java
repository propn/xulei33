package com.propn.golf.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ClassScanerTest {

    @Test
    public void testGetPackageAllClasses() throws IOException, ClassNotFoundException {

        // // 自定义过滤规则
        List<String> classFilters = new ArrayList<String>();
        // 创建一个扫描处理器，排除内部类 扫描符合条件的类
        ClassScaner handler = new ClassScaner(true, true, classFilters);
        long a = System.currentTimeMillis();
        Set<Class<?>> calssList = handler.getPackageAllClasses("com", true);
        long b = System.currentTimeMillis();

        System.out.println(b - a);
        System.out.println(calssList.size());

    }

}
