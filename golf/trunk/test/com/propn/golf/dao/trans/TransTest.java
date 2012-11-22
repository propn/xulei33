package com.propn.golf.dao.trans;

import org.junit.Test;

public class TransTest {

    @Test
    public void testCallTrans() {
        try {
            Trans.transNew(new Trans() {
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
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.transNew(new Trans() {
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
