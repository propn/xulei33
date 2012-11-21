package com.propn.golf.dao.trans;

import java.util.List;

import com.propn.golf.dao.Person;
import com.propn.golf.dao.sql.Po;

public class TransTest {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {

            final Person p = new Person();
            p.set("age", 28);
            p.set("personId", "1");
            p.set("personName", "徐雷");
            Trans.call(new TransAtom() {
                @Override
                public Object call() throws Exception {
                    // 删除
                    System.out.println("--------------------------------------");
                    p.delete();
                    // 新增
                    System.out.println("--------------------------------------");
                    p.save();
                    System.out.println("--------------------------------------");
                    p.set("personName", "东升");
                    p.save();
                    System.out.println("--------------------------------------");
                    try {
                        Trans.call(Trans.NEST, new TransAtom() {
                            @Override
                            public Object call() throws Exception {
                                System.out.println("--------------------------------------");
                                p.set("personName", "俊岭");
                                p.save();
                                System.out.println("--------------------------------------");
                                p.delete();
                                throw new Exception();
                                // return null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("--------------------------------------");
                    List<Po> list = p.getList();
                    for (Po p : list) {
                        System.out.print(p.get("personName") + "  ");
                        System.out.print(p.get("personId") + "  ");
                        System.out.println(p.get("age"));
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
