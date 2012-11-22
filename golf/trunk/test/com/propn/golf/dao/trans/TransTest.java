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
            try {

                Trans.call(new TransAtom() {
                    @Override
                    public Object call() throws Exception {
                        // 清空数据库数据
                        System.out.println("---------------清空数据库数据-------------");
                        p.delete();
                        return null;
                    }
                });

                Trans.call(new TransAtom() {
                    @Override
                    public Object call() throws Exception {
                        System.out.println("---------------查询数据库数据-------------");
                        List<Po> list = p.getList();
                        for (Po p : list) {
                            System.out.print(p.get("personName") + "  ");
                            System.out.print(p.get("personId") + "  ");
                            System.out.println(p.get("age"));
                        }

                        // 新增
                        System.out.println("---------------新增数据徐雷-------------");
                        p.save();

                        System.out.println("---------------开始嵌入事务处理--------------");
                        try {
                            Trans.call(Trans.NEST, new TransAtom() {
                                @Override
                                public Object call() throws Exception {

                                    System.out.println("--------------新增数据俊岭-------------");
                                    p.set("personName", "俊岭");
                                    p.save();

                                    System.out.println("---------------开始独立事务处理--------------");
                                    Trans.call(Trans.NEW, new TransAtom() {
                                        @Override
                                        public Object call() throws Exception {
                                            System.out.println("-----------新增数据亮亮----------------");
                                            p.set("personName", "亮亮");
                                            p.save();

                                            System.out.println("------------开始嵌套事务处理-----------------");
                                            Trans.call(Trans.NEST, new TransAtom() {
                                                @Override
                                                public Object call() throws Exception {
                                                    System.out.println("-----------新增数据东升----------------");
                                                    p.set("personName", "东升");
                                                    p.save();
                                                    return null;
                                                }
                                            });
                                            return null;
                                        }
                                    });
                                    System.out.println("---------------独立事务完成提交--------------");
                                    System.out.println("--------------嵌入事务回滚-----------");
                                    throw new Exception();
                                }
                            });

                        } catch (Exception e) {
                            throw e;
                        }

                        System.out.println("-------------查询数据库记录---------------");
                        list = p.getList();
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

            // ............
            System.out.println("--------------------------------------");
            List<Po> pos = (List<Po>) Trans.call(new TransAtom() {
                @Override
                public Object call() throws Exception {
                    List<Po> list = p.getList();
                    return list;
                }
            });
            System.out.println("--------------------------------------");
            for (Po p1 : pos) {
                System.out.print(p1.get("personName") + "  ");
                System.out.print(p1.get("personId") + "  ");
                System.out.println(p1.get("age"));
            }
            System.out.println("--------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
