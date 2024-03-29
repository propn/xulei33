package com.propn.golf.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class ClassUtils {

    /**
     * 是否排除内部类 true->是 false->否
     */
    private boolean excludeInner = true;

    /**
     * 过滤规则适用情况 true—>搜索符合规则的 false->排除符合规则的
     */
    private boolean checkInOrEx = true;

    /**
     * 过滤规则列表 如果是null或者空，即全部符合不过滤
     */
    private List<String> classFilters = null;

    /**
     * 无参构造器，默认是排除内部类、并搜索符合规则
     */
    public ClassUtils() {
    }

    /**
     * excludeInner:是否排除内部类 true->是 false->否<br>
     * checkInOrEx：过滤规则适用情况 true—>搜索符合规则的 false->排除符合规则的<br>
     * classFilters：自定义过滤规则，如果是null或者空，即全部符合不过滤
     * 
     * @param excludeInner
     * @param checkInOrEx
     * @param classFilters
     */
    public ClassUtils(Boolean excludeInner, Boolean checkInOrEx, List<String> classFilters) {
        this.excludeInner = excludeInner;
        this.checkInOrEx = checkInOrEx;
        this.classFilters = classFilters;
    }

    public static Set<Class<?>> getPackageAllClasses(String... pkgs) throws IOException, ClassNotFoundException {
        ClassUtils handler = new ClassUtils(true, true, null);
        Set<Class<?>> clzs = new LinkedHashSet<Class<?>>();
        for (String pkg : pkgs) {
            clzs.addAll(handler.getPackageAllClasses(pkg, true));
        }
        return clzs;
    }

    /**
     * 扫描包
     * 
     * @param basePackage 基础包
     * @param recursive 是否递归搜索子包
     * @return Set
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Set<Class<?>> getPackageAllClasses(String basePackage, boolean recursive) throws IOException,
            ClassNotFoundException {
        if (null == basePackage) {
            throw new IOException("basePackage 不能为空!");
        }
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageName = basePackage;
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        }
        String package2Path = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(package2Path);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    doScanPackageClassesByFile(classes, packageName, filePath, recursive);
                } else if ("jar".equals(protocol)) {
                    doScanPackageClassesByJar(packageName, url, recursive, classes);
                }
            }
        } catch (IOException e) {
            throw e;
        }

        return classes;
    }

    /**
     * 以jar的方式扫描包下的所有Class文件<br>
     * 
     * @param basePackage eg：michael.utils.
     * @param url
     * @param recursive
     * @param classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void doScanPackageClassesByJar(String basePackage, URL url, final boolean recursive, Set<Class<?>> classes)
            throws ClassNotFoundException, IOException {
        String packageName = basePackage;
        String package2Path = packageName.replace('.', '/');
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(package2Path) || entry.isDirectory()) {
                    continue;
                }

                // 判断是否递归搜索子包
                if (!recursive && name.lastIndexOf('/') != package2Path.length()) {
                    continue;
                }
                // 判断是否过滤 inner class
                if (this.excludeInner && name.indexOf('$') != -1) {
                    continue;
                }
                String classSimpleName = name.substring(name.lastIndexOf('/') + 1);
                // 判定是否符合过滤条件
                if (this.filterClassName(classSimpleName)) {
                    String className = name.replace('/', '.');
                    className = className.substring(0, className.length() - 6);
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                    } catch (ClassNotFoundException e) {
                        throw e;
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 以文件的方式扫描包下的所有Class文件
     * 
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     * @throws ClassNotFoundException
     */
    private void doScanPackageClassesByFile(Set<Class<?>> classes, String packageName, String packagePath,
            boolean recursive) throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        final boolean fileRecursive = recursive;
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return fileRecursive;
                }
                String filename = file.getName();
                if (excludeInner && filename.indexOf('$') != -1) {
                    return false;
                }
                return filterClassName(filename);
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                doScanPackageClassesByFile(classes, packageName + "." + file.getName(), file.getAbsolutePath(),
                        recursive);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));

            }
        }
    }

    /**
     * 根据过滤规则判断类名
     * 
     * @param className
     * @return
     */
    private boolean filterClassName(String className) {
        if (!className.endsWith(".class")) {
            return false;
        }
        if (null == this.classFilters || this.classFilters.isEmpty()) {
            return true;
        }
        String tmpName = className.substring(0, className.length() - 6);
        boolean flag = false;
        for (String str : classFilters) {
            String tmpreg = "^" + str.replace("*", ".*") + "$";
            Pattern p = Pattern.compile(tmpreg);
            if (p.matcher(tmpName).find()) {
                flag = true;
                break;
            }
        }
        return (checkInOrEx && flag) || (!checkInOrEx && !flag);
    }

    /**
     * @return the excludeInner
     */
    public boolean isExcludeInner() {
        return excludeInner;
    }

    /**
     * @return the checkInOrEx
     */
    public boolean isCheckInOrEx() {
        return checkInOrEx;
    }

    /**
     * @return the classFilters
     */
    public List<String> getClassFilters() {
        return classFilters;
    }

    /**
     * @param pExcludeInner the excludeInner to set
     */
    public void setExcludeInner(boolean pExcludeInner) {
        excludeInner = pExcludeInner;
    }

    /**
     * @param pCheckInOrEx the checkInOrEx to set
     */
    public void setCheckInOrEx(boolean pCheckInOrEx) {
        checkInOrEx = pCheckInOrEx;
    }

    /**
     * @param pClassFilters the classFilters to set
     */
    public void setClassFilters(List<String> pClassFilters) {
        classFilters = pClassFilters;
    }

    /**
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 自定义过滤规则
        List<String> classFilters = new ArrayList<String>();
        classFilters.add("File*");
        // 创建一个扫描处理器，排除内部类 扫描符合条件的类
        ClassUtils handler = new ClassUtils(true, true, classFilters);
        System.out.println("开始递归扫描jar文件的包：org.apache.commons.io 下符合自定义过滤规则的类...");
        Set<Class<?>> calssList = handler.getPackageAllClasses("org.apache.commons.io", true);
        for (Class<?> cla : calssList) {
            System.out.println(cla.getName());
        }
        System.out.println("开始递归扫描file文件的包：michael.hessian 下符合自定义过滤规则的类...");
        classFilters.clear();
        classFilters.add("Hessian*");
        calssList = handler.getPackageAllClasses("michael.hessian", true);
        for (Class<?> cla : calssList) {
            System.out.println(cla.getName());
        }
    }
}
