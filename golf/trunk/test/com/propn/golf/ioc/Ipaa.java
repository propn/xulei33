package com.propn.golf.ioc;

import javax.inject.Named;

@Named(value = "ipaa")
public class Ipaa extends Ipa {

    @Override
    public void sayHello() {
        System.out.println("hello ipaa" + this.getClass().getName());
    }

}
