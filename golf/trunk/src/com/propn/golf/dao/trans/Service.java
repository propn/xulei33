/**
 * 
 */
package com.propn.golf.dao.trans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.dao.ds.ConnUtils;

/**
 * @author Thunder.xu
 * 
 */
public abstract class Service {
    private static final Logger log = LoggerFactory.getLogger(Service.class);

    /**
     * 执行一组原子操作，默认的事务级别为: TRANSACTION_READ_COMMITTED。 事务传播为 PROPAGATION_REQUIRED
     * 
     * @param atoms 原子操作对象
     * @throws Exception
     */
    public static Object call(Atom... atoms) throws Exception {
        System.out.println(Thread.currentThread().getId());
        return call(0, atoms);
    }

    /**
     * 
     * @param propagation_requires_new
     * @param atoms
     */
    public static Object call(boolean propagation_requires_new, Atom... atoms) throws Exception {
        if (propagation_requires_new) {
            return call(Trans.REQUIRES_NEW, atoms);
        } else {
            return call(Trans.REQUIRED, atoms);
        }
    }

    /**
     * 服务调用
     * 
     * @param propagation : 0-PROPAGATION_REQUIRED ,1-PROPAGATION_REQUIRES_NEW
     * @param atoms
     * @throws Exception
     */
    private static Object call(int propagation, Atom... atoms) throws Exception {
        String transId = ConnUtils.getTransId();
        if (null == transId) {
            ConnUtils.setTransId("1"); // 初始化上下文事务
        }
        if (propagation == Trans.REQUIRES_NEW) {// 独立隔离事务，独立提交,回滚影响父事务
            if (null != transId) {
                ConnUtils.setTransId(ConnUtils.getTransId() + (ConnUtils.getCurrentTransId() + 1));
            }
            for (Atom atom : atoms) {
                try {
                    atom.call();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    ConnUtils.rollbackAll();
                    throw new RuntimeException(e);
                }
            }
            ConnUtils.commit();
        } else if (propagation == Trans.REQUIRED) {// 同父事务在同一个事务中
            if (null != transId) {
                ConnUtils.setTransId(transId + "0");
            }
            for (Atom atom : atoms) {
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
        } else if (propagation == Trans.REQUIRED_NEW_ALONE) {
            // 独立隔离事务，独立提交回滚,不影响父事务
            if (null != transId) {
                ConnUtils.setTransId(ConnUtils.getTransId() + (ConnUtils.getCurrentTransId() + 1));
            }
            Exception err = null;
            for (Atom atom : atoms) {
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
