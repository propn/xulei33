package com.propn.golf.mvc;

public class ThreadLocaltest {

    private final static InheritableThreadLocal<String> holder = new InheritableThreadLocal<String>();

    public static void main(String[] args) {
        holder.set("aaa");
        System.out.println("begin=" + holder.get());

        Thread a = new Thread() {
            public void run() {
                System.out.println("thread-begin=" + holder.get());
                holder.set("vvvvvvvvvvvvv");
                System.out.println("thread-end=" + holder.get());
            }
        };
        a.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end=" + holder.get());
    }

}
