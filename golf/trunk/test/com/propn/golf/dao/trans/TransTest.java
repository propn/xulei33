package com.propn.golf.dao.trans;

import static org.junit.Assert.*;

import org.junit.Test;

public class TransTest {

    @Test
    public void testCallTrans() {
        try {
            Trans.call(new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCallIntTrans() {
        try {
            Trans.call(Trans.REQUIRED, new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.call(Trans.NEW, new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.call(Trans.NEST, new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
