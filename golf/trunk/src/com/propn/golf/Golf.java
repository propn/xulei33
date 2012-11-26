/**
 * 
 */
package com.propn.golf;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.ioc.BeanUtils;
import com.propn.golf.mvc.ResUtils;
import com.propn.golf.tools.ClassUtils;
import com.propn.golf.tools.XmlUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Golf {
    private static final Logger log = LoggerFactory.getLogger(Golf.class);

    protected static String appPath = null;
    protected static String packages[] = null;

    public static String getAppPath() {
        return appPath;
    }

    public static void init(String... pkgs) throws Exception {
        if (null == pkgs || pkgs.length == 0) {
            pkgs = getPkgs();
            packages = pkgs;
        }
        Set<Class<?>> clzs = ClassUtils.getPackageAllClasses(pkgs);
        // jaxb
        XmlUtils.regist(clzs);
        for (Class<?> clz : clzs) {
            // Resist mvc
            ResUtils.registerResouce(clz);
            // Resist bean
            BeanUtils.registBean(clz);
        }
    }

    public static String[] getPkgs() {

        if (null != packages && packages.length != 0) {
            return packages;
        }

        String classPath = null;// classPath
        if (null != appPath) {
            classPath = appPath + "WEB-INF\\classes";
        } else {
            classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        }
        File dir = new File(classPath);
        File[] files = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            @Override
            public boolean accept(File file) {
                if (file.isDirectory() && !file.getName().equals("javax") && !file.getName().equals("java")) {
                    return true;
                }
                return false;
            }
        });

        String[] pkgs = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            pkgs[i] = files[i].getName();
        }
        // 设置
        packages = pkgs;
        return pkgs;
    }

}
