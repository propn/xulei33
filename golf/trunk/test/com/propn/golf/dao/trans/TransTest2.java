package com.propn.golf.dao.trans;

import java.util.List;

import org.junit.Test;

import com.propn.golf.dao.Person;
import com.propn.golf.dao.sql.Po;

public class TransTest2 {

    @Test
    public void T2() {

        final Person p = new Person();
        try {
            p.set("personId", "1");
            p.set("personName", "徐雷");
            p.set("age", 28);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        System.out.println("---------------------------------------------");
        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    p.delete();
                    return null;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------");
        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    p.save();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------");
        try {
            Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    p.set("personName", "东升");
                    p.update();
                    return null;
                    // throw new Exception();
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------");
        try {
            List<Po> ps = (List<Po>) Trans.transNew(new Trans() {
                @Override
                public Object call() throws Exception {
                    return p.qryList();
                }
            });
            for (Po po : ps) {
                System.out.println(po.get("personName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
