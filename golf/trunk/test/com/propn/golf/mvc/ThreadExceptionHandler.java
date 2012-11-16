package com.propn.golf.mvc;

public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getName() + ": " + e.getMessage());
        System.out.println(e.getClass().getName());
    }
}