package com.propn.golf.dao.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.Constants;

public class ConnUtils {

    private static final Logger log = LoggerFactory.getLogger(ConnUtils.class);

    private static final ThreadLocal<String> transId = new ThreadLocal<String>();// 当前事务ID
    private static final ThreadLocal<Map<Integer, Map<String, Connection>>> transCtx = new ThreadLocal<Map<Integer, Map<String, Connection>>>();
    private static final ThreadLocal<Map<Integer, Map<String, Savepoint>>> savePointCtx = new ThreadLocal<Map<Integer, Map<String, Savepoint>>>();

    public static String getTransId() {
        return transId.get();
    }

    public static void setTransId(String id) {
        transId.set(id);
    }

    public static int getCurrentTransId() {
        String id = transId.get();
        if (null == id) {
            return 0;
        }
        char[] ids = id.toCharArray();
        for (int i = ids.length - 1; i >= 0;) {
            if (ids[i] != '0') {
                return Integer.valueOf(String.valueOf(ids[i]));
            }
            i--;
        }
        return 0;
    }

    /**
     * 
     * @param dsCode
     * @return
     * @throws Exception
     */
    public static Connection getConn(String dsCode) throws Exception {
        if (null == getTransId()) {
            throw new Exception("当前操作不在数据库事务中,请使用Service.call进行数据库操作！");
        }
        int currentTransId = getCurrentTransId();
        if (null == dsCode) {
            dsCode = Constants.DEFAULT_DATASOURCE;
        }

        Map<Integer, Map<String, Connection>> transCache = transCtx.get();// {currenttransId,{dsCode,Connection}}
        Connection conn = null;
        if (null == transCache)// 事务Cache
        {
            transCache = Collections.synchronizedMap(new HashMap<Integer, Map<String, Connection>>());
            log.debug("init ThreadLocal transCache");
            Map<String, Connection> connCache = Collections.synchronizedMap(new HashMap<String, Connection>());
            log.debug("init ThreadLocal connCache");
            conn = DsUtils.getDataSource(dsCode).getConnection();
            connCache.put(dsCode, conn);
            log.debug("init ThreadLocal Conn");
            transCache.put(getCurrentTransId(), connCache);
            transCtx.set(transCache);
            log.debug("init transCtx");
        } else {
            Map<String, Connection> connCache = transCache.get(currentTransId);
            if (null == connCache) {
                connCache = Collections.synchronizedMap(new HashMap<String, Connection>());
                log.debug("init ThreadLocal connCache");
                conn = DsUtils.getDataSource(dsCode).getConnection();
                connCache.put(dsCode, conn);
                log.debug("init ThreadLocal Conn");
                transCache.put(getCurrentTransId(), connCache);
                log.debug("init transCtx");
            } else {
                conn = connCache.get(dsCode);
                if (null == conn) {
                    conn = DsUtils.getDataSource(dsCode).getConnection();
                    connCache.put(dsCode, conn);
                    log.debug("init {} cache ", dsCode);
                }
            }
        }
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        log.debug("getConn:{} . Thread:{} .", dsCode, Thread.currentThread().getId());
        // 嵌套事务
        if (3 == getCurrentTransId()) {
            Map<Integer, Map<String, Savepoint>> savePointCache = savePointCtx.get();// {currenttransId,{dsCode,savePoint}}
            if (null == savePointCache) {
                savePointCache = Collections.synchronizedMap(new HashMap<Integer, Map<String, Savepoint>>());
                savePointCtx.set(savePointCache);
                Map<String, Savepoint> savepoints = Collections.synchronizedMap(new HashMap<String, Savepoint>());
                Map<String, Connection> connCache = transCache.get(currentTransId);
                Set<String> dsCodes = connCache.keySet();
                for (String ds : dsCodes) {
                    Connection c = connCache.get(ds);
                    savepoints.put(ds, c.setSavepoint());
                }
                savePointCache.put(getCurrentTransId(), savepoints);
            } else {
                Map<String, Savepoint> savepoints = savePointCache.get(getCurrentTransId());
                Savepoint p = savepoints.get(dsCode);
                if (null == p) {
                    p = conn.setSavepoint();
                    savepoints.put(dsCode, p);
                }
            }
        }
        return conn;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        return getConn(null);
    }

    /**
     * @throws SQLException
     * @throws Exception
     * 
     */
    public static void commit() {
        if (null == transCtx.get() || null == transCtx.get().get(getCurrentTransId())) {
            return;
        }
        Map<String, Connection> cache = transCtx.get().get(getCurrentTransId());
        for (Connection conn : cache.values()) {
            if (conn != null) {
                try {
                    conn.commit();
                } catch (SQLException e) {
                    log.debug("事务提交失败：" + getCurrentTransId(), e);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.debug("数据库连接关闭失败：" + getCurrentTransId(), e);
                    }
                }
            }
        }
        log.debug("提交并关闭数据库连接 ,事务Id:{} ", getCurrentTransId());
        clean();
    }

    /**
     * @throws SQLException
     * 
     */
    public static void rollback() {
        if (null == transCtx.get() || null == transCtx.get().get(getCurrentTransId())) {
            return;
        }
        rollbackByTransId(getCurrentTransId());
    }

    public static void rollbackByTransId(int id) {
        Map<String, Connection> connCache = transCtx.get().get(id);

        if (null == connCache) {
            return;
        }
        for (Map.Entry<String, Connection> entry : connCache.entrySet()) {
            Connection conn = entry.getValue();
            if (conn != null) {
                try {
                    if (3 == id) {
                        Map<String, Savepoint> savePointCache = savePointCtx.get().get(id);
                        Savepoint savepoint = savePointCache.get(entry.getKey());
                        conn.releaseSavepoint(savepoint);
                    } else {
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    log.debug("事务回滚失败：" + id, e);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.debug("数据库连接关闭失败：" + id, e);
                    }
                }
            }
        }
        log.debug("回滚并关闭数据库连接 ,事务Id:{} ", id);
        cleanById(id);
    }

    public static void rollbackAll() {
        Map<Integer, Map<String, Connection>> map = transCtx.get();
        if (null == map) {
            return;
        }
        Object[] trans = map.keySet().toArray();
        for (int i = 0; i < trans.length; i++) {
            int id = (Integer) trans[i];
            rollbackByTransId(id);
        }
    }

    /**
     * @throws SQLException
     * 
     */
    public static void cleanById(int id) {
        Map<String, Connection> cache = transCtx.get().get(id);
        cache = null;
        transCtx.get().remove(id);
        log.debug("销毁事务对象:{}", id);
    }

    /**
     * @throws SQLException
     * 
     */
    public static void clean() {
        Map<String, Connection> cache = transCtx.get().get(getCurrentTransId());
        cache = null;
        transCtx.get().remove(getCurrentTransId());
        log.debug("销毁事务对象:{}", getCurrentTransId());
    }

}
