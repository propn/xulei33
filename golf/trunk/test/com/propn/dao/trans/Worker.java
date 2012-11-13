package com.propn.dao.trans;

import java.util.concurrent.Callable;

public class Worker implements Callable<Object> {

    private int hours = 12;
    private int amount;

    public Worker(int hours, int amount) {
        this.hours = hours;
        this.amount = amount;
    }

    @Override
    public Integer call() throws Exception {
        while (hours > 0) {
            System.out.println("I'm working...... " + Thread.currentThread().getId());
            amount++;
            hours--;
            Thread.sleep(1000);
        }
        if(true){
            throw new RuntimeException();
        }
        return amount;
    }
}