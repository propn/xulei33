package com.propn.golf.dao.trans;

import static org.junit.Assert.*;

import org.junit.Test;

public class TransTest {

    @Test
    public void testCallTransAtom() {
        try {
            Trans.call(new TransAtom() {
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
    public void testCallIntTransAtom() {
        try {
            Trans.call(Trans.REQUIRED, new TransAtom() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.call(Trans.NEW, new TransAtom() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trans.call(Trans.NEST, new TransAtom() {
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
