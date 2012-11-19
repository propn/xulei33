/**
 * 
 */
package com.propn.golf.dao.trans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.dao.ds.ConnUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Service {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    /* 默认,当前上下文存在事务则使用当前事务,没有则新建事务 */
    public static int REQUIRED = 0;
    /* 建嵌套事务 同一个Connection 需数据库支持Savepoint特性,不自己Commit,由上下文事务一起Commit */
    public static int REQUIRED_NEST = 3;

    /* 新建事务,使用新数据库连接,独立Commit和rollbcak */
    public static int REQUIRED_NEW = 1;
    /* 新建事务,使用新数据库连接,不抛异常 */
    public static int REQUIRED_NEW_ALONE = 2;

    /**
     * 执行一组原子操作，默认的事务级别为: TRANSACTION_READ_COMMITTED。 事务传播为 PROPAGATION_REQUIRED
     * 
     * @param atoms 原子操作对象
     * @throws Exception
     */
    public static Object call(TransAtom... atoms) throws Exception {
        return call(0, atoms);
    }

    /**
     * 
     * @param propagation_requires_new
     * @param atoms
     */
    public static Object call(boolean propagation_requires_new, TransAtom... atoms) throws Exception {
        if (propagation_requires_new) {
            return call(REQUIRED_NEW, atoms);
        } else {
            return call(REQUIRED, atoms);
        }
    }

    /**
     * 服务调用
     * 
     * @param propagation : 0-PROPAGATION_REQUIRED ,1-PROPAGATION_REQUIRES_NEW
     * @param atoms
     * @throws Exception
     */
    public static Object call(int propagation, TransAtom... atoms) throws Exception {
        String transId = ConnUtils.getTransId();
        if (null == transId) {
            ConnUtils.setTransId("1"); // 初始化上下文事务
        }
        if (propagation == REQUIRED) {// 同父事务在同一个事务中
            if (null != transId) {
                ConnUtils.setTransId(transId + "0");
            }
            for (TransAtom atom : atoms) {
                try {
                    atom.call();
                } catch (Exception e) {
                    log.debug("业务处理出错", e);
                    ConnUtils.rollbackAll();
                    throw new RuntimeException(e);
                }
            }
            if (null == transId) {
                ConnUtils.commit();
            }
        } else if (propagation == REQUIRED_NEST) {// 嵌套事务,同一个Connection
            if (null != transId) {
                ConnUtils.setTransId(transId + "2");
            }
            for (TransAtom atom : atoms) {
                try {
                    atom.call();
                } catch (Exception e) {
                    log.debug("业务处理出错", e);
                    ConnUtils.rollback();
                    throw new RuntimeException(e);
                }
            }
            if (null == transId) {
                ConnUtils.commit();
            }
        } else if (propagation == REQUIRED_NEW) {// 独立隔离事务，独立提交,回滚影响父事务
            if (null != transId) {
                ConnUtils.setTransId(ConnUtils.getTransId() + (ConnUtils.getCurrentTransId() + 1));
            }
            for (TransAtom atom : atoms) {
                try {
                    atom.call();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    ConnUtils.rollbackAll();
                    throw new RuntimeException(e);
                }
            }
            ConnUtils.commit();
        } else if (propagation == REQUIRED_NEW_ALONE) {
            // 独立隔离事务，独立提交回滚,不影响父事务
            if (null != transId) {
                ConnUtils.setTransId(ConnUtils.getTransId() + (ConnUtils.getCurrentTransId() + 1));
            }
            Exception err = null;
            for (TransAtom atom : atoms) {
                try {
                    atom.call();
                } catch (Exception e) {
                    err = e;
                    log.debug(e.getMessage());
                    ConnUtils.rollback();
                }
            }
            if (err != null) {
                ConnUtils.commit();
                if (null == transId) {
                    throw new RuntimeException(err);
                }
            }
        }

        ConnUtils.setTransId(transId);
        return null;
    }
}
