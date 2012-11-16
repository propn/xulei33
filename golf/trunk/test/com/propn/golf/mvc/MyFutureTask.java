package com.propn.golf.mvc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MyFutureTask extends FutureTask<Object> {

    public MyFutureTask(Callable<Object> callable) {
        super(callable);
    }
    
    @Override
    protected void done() {
        try {
            System.out.println(Thread.currentThread().getId());
            if (!isCancelled()){
                get();
            }
        } catch (ExecutionException e) {
            System.out.println(Thread.currentThread().getId());
            System.out.println("Exception:111111 " + e.getCause());
        } catch (InterruptedException e) {
            System.out.println("Exception:222222 " + e.getCause());
            throw new AssertionError(e);
        }
    }
}
