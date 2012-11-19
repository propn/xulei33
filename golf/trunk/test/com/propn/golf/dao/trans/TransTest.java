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
            Service.call(new TransAtom() {
                @Override
                public Object call() throws Exception {
                    // 删除
                    p.delete();
                    // 增加
                    p.save();
                    // 修改
                    p.set("personName", "张东升");
                    p.update();
                    // 查询
                    List<Po> rsts = p.getList();
                    for (Po po : rsts) {
                        System.out.print(po.get("age") + "   ");
                        System.out.print(po.get("personId") + "   ");
                        System.out.print(po.get("personName") + "   ");
                        System.out.println();
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
