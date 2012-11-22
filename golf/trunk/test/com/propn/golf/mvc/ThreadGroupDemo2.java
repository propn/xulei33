package com.propn.golf.mvc;

import java.util.concurrent.FutureTask;


public class ThreadGroupDemo2 {
    public static void main(String[] args) {
        // 建立异常处理者
        ThreadExceptionHandler handler = new ThreadExceptionHandler();
        ThreadGroup threadGroup1 = new ThreadGroup("group1");
        
        Atom atom = new Atom(null, null, null);
        FutureTask<Object> transMgr = new FutureTask<Object>(atom);
        Thread t = new Thread(threadGroup1, transMgr);
        
        // 这是匿名类写法
        Thread thread1 =
        // 这个线程是threadGroup1的一员
        new Thread(threadGroup1, new Runnable() {
            public void run() {
                // 抛出unchecked异常
                throw new RuntimeException("测试异常");
            }
        });
        // 设置异常处理者
        thread1.setUncaughtExceptionHandler(handler);
        thread1.start();
        
        System.out.println(121221);
    }
}