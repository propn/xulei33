package com.propn.golf.dao.trans;

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

    /* 当前线程事务状态,transId序列 */
    private static final ThreadLocal<String> transStatus = new ThreadLocal<String>();
    /* {CurrentTransId,{dsCode,Connection}} */
    private static final ThreadLocal<Map<Integer, Map<String, Connection>>> connCtx = new ThreadLocal<Map<Integer, Map<String, Connection>>>();
    /* {transStatus,{dsCode,SavePoint}} */
    private static final ThreadLocal<Map<String, Map<String, Savepoint>>> savePointCtx = new ThreadLocal<Map<String, Map<String, Savepoint>>>();

    static String getTransStatus() {
        return transStatus.get();
    }

    static void setTransStatus(String status) {
        transStatus.set(status);
    }

    static int getCurrentTransId() {
        String id = transStatus.get();
        if (null == id) {
            return 0;
        }
        char[] ids = id.toCharArray();
        for (int i = ids.length - 1; i >= 0;) {
            if (Integer.valueOf("" + ids[i]) == Trans.NEW) {
                return Integer.valueOf(String.valueOf(ids[i]));
            }
            i--;
        }
        return 0;
    }

    private static int getCurrentPropagation() {
        String id = transStatus.get();
        if (null == id || id.length() == 0) {
            return 0;
        }
        return Integer.parseInt(id.substring(id.length() - 1));
    }

    /**
     * 
     * @param dsCode
     * @return
     * @throws Exception
     */
    public static Connection getConn(String dsCode) throws Exception {
        log.debug("currentTransStatus  " + transStatus.get());
        if (null == getTransStatus()) {
            throw new Exception("当前操作不在数据库事务中,请使用Service.call进行数据库操作！");
        }

        int currentTransId = getCurrentTransId();
        log.debug("currentTransId   " + currentTransId);

        if (null == dsCode) {
            dsCode = Constants.DEFAULT_DATASOURCE;
        }

        Connection conn = null;
        Map<Integer, Map<String, Connection>> connCache = connCtx.get();// {currenttransId,{dsCode,Connection}}
        if (null == connCache)// 事务Cache
        {
            connCache = Collections.synchronizedMap(new HashMap<Integer, Map<String, Connection>>());
            log.debug("init ThreadLocal transCache");
            Map<String, Connection> connMap = Collections.synchronizedMap(new HashMap<String, Connection>());
            log.debug("init ThreadLocal connCache");
            conn = DsUtils.getDataSource(dsCode).getConnection();
            connMap.put(dsCode, conn);
            log.debug("init ThreadLocal Conn");
            connCache.put(currentTransId, connMap);
            connCtx.set(connCache);
            log.debug("init transCtx");
        } else {
            Map<String, Connection> connMap = connCache.get(currentTransId);
            if (null == connMap) {
                connMap = Collections.synchronizedMap(new HashMap<String, Connection>());
                log.debug("init ThreadLocal connCache");
                conn = DsUtils.getDataSource(dsCode).getConnection();
                connMap.put(dsCode, conn);
                log.debug("init ThreadLocal Conn");
                connCache.put(currentTransId, connMap);
                log.debug("init transCtx");
            } else {
                conn = connMap.get(dsCode);
                if (null == conn) {
                    conn = DsUtils.getDataSource(dsCode).getConnection();
                    connMap.put(dsCode, conn);
                    log.debug("init {} cache ", dsCode);
                }
            }
        }
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        log.debug("getConn:{} . Thread:{} .", dsCode, Thread.currentThread().getId());

        // 嵌套事务
        if (Trans.NEST == getCurrentPropagation()) {
            Map<String, Map<String, Savepoint>> savePointCache = savePointCtx.get();// {currenttransId,{dsCode,savePoint}}
            if (null == savePointCache) {
                savePointCache = Collections.synchronizedMap(new HashMap<String, Map<String, Savepoint>>());
                /* {dsCode,SavePoint} */
                Map<String, Savepoint> savepoints = Collections.synchronizedMap(new HashMap<String, Savepoint>());
                /* 为上下文中所有Connection创建SavePoint */
                Map<String, Connection> connMap = connCache.get(currentTransId);
                Set<String> dsCodes = connMap.keySet();
                for (String ds : dsCodes) {
                    Connection c = connMap.get(ds);
                    savepoints.put(ds, c.setSavepoint());
                }
                savePointCache.put(getTransStatus(), savepoints);
                savePointCtx.set(savePointCache);
            } else {
                Map<String, Savepoint> savepoints = savePointCache.get(getTransStatus());
                if (null == savepoints) {
                    savepoints = Collections.synchronizedMap(new HashMap<String, Savepoint>());
                    savePointCache.put(getTransStatus(), savepoints);
                    Map<String, Connection> connMap = connCache.get(currentTransId);
                    Connection c = connMap.get(dsCode);
                    savepoints.put(dsCode, c.setSavepoint());
                } else {
                    Savepoint p = savepoints.get(dsCode);
                    if (null == p) {
                        p = conn.setSavepoint();
                        savepoints.put(dsCode, p);
                    }
                }
            }
        }
        System.out.println(conn.toString());
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
        if (null == connCtx.get() || null == connCtx.get().get(getCurrentTransId())) {
            return;
        }
        Map<String, Connection> cache = connCtx.get().get(getCurrentTransId());
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
        if (null == connCtx.get() || null == connCtx.get().get(getCurrentTransId())) {
            return;
        }
        rollbackByTransId(getCurrentTransId());
    }

    public static void rollbackByTransId(int id) {
        Map<String, Connection> connCache = connCtx.get().get(id);

        if (null == connCache) {
            return;
        }
        for (Map.Entry<String, Connection> entry : connCache.entrySet()) {
            Connection conn = entry.getValue();
            if (conn != null) {
                try {
                    if (Trans.NEST == id) {
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
        Map<Integer, Map<String, Connection>> map = connCtx.get();
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
        Map<String, Connection> cache = connCtx.get().get(id);
        cache = null;
        connCtx.get().remove(id);
        log.debug("销毁事务对象:{}", id);
    }

    /**
     * @throws SQLException
     * 
     */
    public static void clean() {
        Map<String, Connection> cache = connCtx.get().get(getCurrentTransId());
        cache = null;
        connCtx.get().remove(getCurrentTransId());
        log.debug("销毁事务对象:{}", getCurrentTransId());
    }

}
