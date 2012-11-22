package com.propn.golf.dao;

import com.propn.golf.dao.sql.Po;
import com.propn.golf.dao.trans.Trans;

public class PersonTest {

    public static void main(String[] args) throws Exception {

        Person p = new Person();
        p.set("personName", "Thunder");
        p.set("age", 28);

        System.out.println(p.get("personName"));
        System.out.println(p.get("age"));

        //
        Po s = new Student();
        s.set("personName", "Thunder.hsu");
        s.set("age", 28);
        //
        s.set("grade", 2003);
        s.set("major", "计算机");
        s.set("Counselor", p);

        System.out.println(s.get("personName"));
        System.out.println(s.get("age"));

        System.out.println(s.get("grade"));
        System.out.println(s.get("major"));

        final Person t = (Person) s.get("Counselor");

        if (p.equals(t)) {
            System.out.println("1212");
            System.out.println(p.get("personName"));
        }

        System.out.println("currentThread :" + Thread.currentThread().getId());

        Trans.call(new Trans() {
            @Override
            public Object call() throws Exception {
                t.save();
                return null;
            }
        });
    }
}
