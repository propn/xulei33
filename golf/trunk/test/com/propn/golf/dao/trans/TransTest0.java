package com.propn.golf.dao.trans;

import org.junit.Test;

public class TransTest0 {

    @Test
    public void T0() {
        System.out.println("----------------------1-----------------------");
        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                    // throw new Exception();
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
        }
        System.out.println("----------------------1-----------------------");

    }

    @Test
    public void T1() {
        System.out.println("----------------------1-----------------------");
        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    try {
                        System.out.println("----------------------10-----------------------");
                        Trans.transNew(new Trans() {
                            @Override
                            public Object call() throws Exception {
                                return null;
                            }
                        });
                        System.out.println("----------------------10-----------------------");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----------------------1-----------------------");

    }

    @Test
    public void T2() {

        try {
            System.out.println("----------------------1-----------------------");
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    try {
                        System.out.println("----------------------10-----------------------");
                        Trans.transNew(new Trans() {
                            @Override
                            public Object call() throws Exception {
                                throw new Exception();
                            }
                        });
                        System.out.println("----------------------10-----------------------");
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                    return null;
                }
            });
            System.out.println("----------------------1-----------------------");
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

}
