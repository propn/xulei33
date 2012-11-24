/**
 * 
 */
package com.propn.golf.ioc;

import javax.inject.Inject;

/**
 * @author Administrator
 * 
 */
public class Di {

    @Inject
    Ip ipaa;
    @Inject
    Ip ipab;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BeanUtils.registBean("com.propn.golf.ioc");
        Di di = BeanUtils.getInstance(Di.class);
        di.ipaa.sayHello();
        di.ipab.sayHello();
    }

}
