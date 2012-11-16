package com.propn.golf.mvc;

import java.util.concurrent.Callable;

public class Atom2 implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        System.out.println(Thread.currentThread().getId());
        if (true) {
            throw new RuntimeException();
        }
        return "aaaa";
    }
}
