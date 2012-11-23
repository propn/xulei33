/**
 * 
 */
package com.propn.golf.dao.trans;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Trans implements Callable<Object> {

    private static final Logger log = LoggerFactory.getLogger(Trans.class);

    /* 新建事务,独立Commit和rollbcak */
    static int NEW = 1;
    /* 嵌套事务,同一个Connection,由上下文事务一起Commit */
    static int NEST = 2;

    public static Object transNest(Trans atom) throws Exception {
        return call(NEST, atom);
    }

    public static Object transNew(Trans atom) throws Exception {
        return call(NEW, atom);
    }

    /**
     * 服务调用
     * 
     * @param atom
     * @throws Exception
     */
    private static Object call(int propagation, Trans atom) throws Exception {
        Object rst = null;
        String trans = ConnUtils.getTransStatus();
        if (null == trans || null == ConnUtils.getConnCtx() || null == ConnUtils.getConnMap()) {
            if (null == trans || "".equals(trans)) {
                trans = "";
                propagation = NEW;
            }
            ConnUtils.setTransStatus(trans);
        }

        if (propagation == NEW) {// 独立隔离事务，独立提交,回滚影响父事务
            String newTrans = trans + NEW;
            ConnUtils.setTransStatus(newTrans);
            log.debug("trans[{}] begin.", newTrans);
            try {
                rst = atom.call();
            } catch (Exception e) {
                ConnUtils.rollback();
                ConnUtils.setTransStatus(trans);
                log.debug("trans[{}] end rollback. ", newTrans);
                log.debug("trans[{}] end. ", newTrans);
                throw e;
            }
            log.debug("trans[{}] begin commit. ", newTrans);
            ConnUtils.commit();
            log.debug("trans[{}] end commit. ", newTrans);
            log.debug("trans[{}] end! ", newTrans);
            ConnUtils.setTransStatus(trans);
        } else if (propagation == NEST) {// 嵌套事务,同一个Connection
            String newTrans = trans + NEST;
            ConnUtils.setTransStatus(newTrans);
            log.debug("trans[{}] begin.", newTrans);
            try {
                rst = atom.call();
            } catch (Exception e) {
                ConnUtils.rollback();
                ConnUtils.setTransStatus(trans);
                log.debug("trans[{}] end rollback. ", newTrans);
                log.debug("trans[{}] end. ", newTrans);
                throw e;
            }
            ConnUtils.commit();
            ConnUtils.setTransStatus(trans);
            log.debug("trans[{}] end. ", newTrans);
        }
        return rst;
    }
}
