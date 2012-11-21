package com.propn.golf.dao.trans;

import com.propn.golf.dao.Person;

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
                    System.out.println("save....");
                    p.save();
                    System.out.println("save....");
                    Trans.call(Trans.NEST, new TransAtom() {
                        @Override
                        public Object call() throws Exception {
                            p.set("personName", "东升");
                            p.update();
                            System.out.println("update....");

                            Trans.call(Trans.NEST, new TransAtom() {
                                @Override
                                public Object call() throws Exception {
                                    p.set("personName", "东升");
                                    p.delete();
                                    System.out.println("delete....");

                                    Trans.call(Trans.NEW, new TransAtom() {
                                        @Override
                                        public Object call() throws Exception {
                                            p.set("personName", "东升");
                                            p.save();
                                            System.out.println("save2....");
                                            return null;
                                        }
                                    });
                                    return null;
                                }
                            });
                            return null;
                        }
                    });
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
