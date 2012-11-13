package com.propn.dao.trans;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Dizhu {

    public static void main(String args[]) {
        Worker worker = new Worker(3,8);
        FutureTask<Object> jiangong = new FutureTask<Object>(worker);
        new Thread(jiangong).start();
        while (!jiangong.isDone()) {
            try {
                System.out.println("看长工做完了没..." + Thread.currentThread().getId());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Object amount;
        try {
            amount = jiangong.get();
            System.out.println("工作做完了,上交了" + amount);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}