package com.propn.golf.ioc;

import javax.inject.Named;

@Named(value = "ipab")
public class Ipab extends Ipa {

    @Override
    public void sayHello() {
        System.out.println("hello ipab" + this.getClass().getName());
    }

}
