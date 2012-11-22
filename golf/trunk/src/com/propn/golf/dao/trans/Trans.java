/**
 * 
 */
package com.propn.golf.dao.trans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Trans {

    private static final Logger log = LoggerFactory.getLogger(Trans.class);

    /* 默认,同一个Connection,当前上下文存在事务则使用当前事务,没有则新建事务 */
    public static int REQUIRED = 0;
    /* 新建事务,使用新数据库连接,独立Commit和rollbcak */
    public static int NEW = 1;
    /* 嵌套事务, 同一个Connection,由上下文事务一起Commit */
    public static int NEST = 2;

    /**
     * 执行一组原子操作，默认的事务级别为: TRANSACTION_READ_COMMITTED。 事务传播为 PROPAGATION_REQUIRED
     * 
     * @param atoms 原子操作对象
     * @throws Exception
     */
    public static Object call(TransAtom atom) throws Exception {
        return call(REQUIRED, atom);
    }

    /**
     * 
     * @param propagation_requires_new
     * @param atom
     */
    public static Object call(boolean propagation_requires_new, TransAtom atom) throws Exception {
        if (propagation_requires_new) {
            return call(NEW, atom);
        } else {
            return call(REQUIRED, atom);
        }
    }

    /**
     * 服务调用
     * 
     * @param atom
     * @throws Exception
     */
    public static Object call(int propagation, TransAtom atom) throws Exception {
        Object rst = null;
        String trans = ConnUtils.getTransStatus();
        if (null == trans) {
            trans = "";
            ConnUtils.setTransStatus(trans);
            propagation = NEW;
        }

        if (propagation == NEW) {// 独立隔离事务，独立提交,回滚影响父事务
            String newTrans = trans + NEW;
            ConnUtils.setTransStatus(newTrans);
            log.debug("trans[{}] begin...", newTrans);
            try {
                rst = atom.call();
            } catch (Exception e) {
                ConnUtils.rollback();
                log.debug("trans[{}] rollback! ", newTrans);
                throw new RuntimeException(e);
            }
            ConnUtils.commit();
            ConnUtils.setTransStatus(trans);
            log.debug("trans[{}] commit! ", newTrans);
        } else if (propagation == REQUIRED) {// 同父事务在同一个事务中
            String newTrans = trans + REQUIRED;
            ConnUtils.setTransStatus(newTrans);
            log.debug("trans[{}] begin...", newTrans);
            try {
                rst = atom.call();
            } catch (Exception e) {
                ConnUtils.rollback();
                log.debug("trans[{}] rollback! ", newTrans);
                throw new RuntimeException(e);
            }
            ConnUtils.commit();
            ConnUtils.setTransStatus(trans);
            log.debug("trans[{}] commit! ", newTrans);
        } else if (propagation == NEST) {// 嵌套事务,同一个Connection
            String newTrans = trans + NEST;
            ConnUtils.setTransStatus(newTrans);
            log.debug("trans[{}] begin...", newTrans);
            try {
                rst = atom.call();
            } catch (Exception e) {
                ConnUtils.rollback();
                log.debug("trans[{}] rollback! ", newTrans);
                throw new RuntimeException(e);
            }
            ConnUtils.commit();
            ConnUtils.setTransStatus(trans);
            log.debug("trans[{}] commit! ", newTrans);
        }
        return rst;
    }
}
