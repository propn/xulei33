package com.propn.golf.mvc;

import java.util.concurrent.ExecutionException;

public class RunTest {
    public static void main(String[] args) {
        Atom2 atom2 = new Atom2();
        MyFutureTask transMgr = new MyFutureTask(atom2);
        System.out.println(Thread.currentThread().getId());
        // ThreadGroup g = new ThreadGroup("g");
        Thread t = new Thread(transMgr);
        // UncaughtExceptionHandler eh = new ThreadExceptionHandler();
        // t.setUncaughtExceptionHandler(eh);
        t.start();
        try {
            Object rst = transMgr.get();
            System.out.println(rst);
            System.out.println(1212);
            System.out.println(2321321);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
