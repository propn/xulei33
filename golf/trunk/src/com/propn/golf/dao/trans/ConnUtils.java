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
    private static final ThreadLocal<Map<String, Map<String, Connection>>> connCtx = new ThreadLocal<Map<String, Map<String, Connection>>>();
    /* {transStatus,{dsCode,SavePoint}} */
    private static final ThreadLocal<Map<String, Map<String, Savepoint>>> savePointCtx = new ThreadLocal<Map<String, Map<String, Savepoint>>>();

    static String getTransStatus() {
        return transStatus.get();
    }

    static void setTransStatus(String status) {
        transStatus.set(status);
    }

    static String getCurrentTransId() {
        String id = transStatus.get();
        if (null == id) {
            return "";
        }
        char[] ids = id.toCharArray();
        for (int i = ids.length - 1; i >= 0;) {
            if (Integer.valueOf("" + ids[i]) == Trans.NEW) {
                // return Integer.valueOf(String.valueOf(ids[i]));
                return id.substring(0, i + 1);
            }
            i--;
        }
        return "";
    }

    private static int getCurrentPropagation() {
        String id = transStatus.get();
        if (null == id || id.length() == 0) {
            return 0;
        }
        return Integer.parseInt(id.substring(id.length() - 1));
    }

    /**
     * 获取最后一个回滚点
     * 
     * @TODO:test
     * @return
     */
    private static String getLastTrans() {
        String trans = getTransStatus();
        if (trans.endsWith(String.valueOf(Trans.NEST))) {
            return trans;
        }
        char[] ids = trans.toCharArray();
        for (int i = ids.length - 1; i >= 0;) {
            if (Integer.valueOf("" + ids[i]) != Trans.REQUIRED) {
                return trans.substring(0, i + 1);
            }
            i--;
        }
        return "";
    }

    /**
     * 
     * @param dsCode
     * @return
     * @throws Exception
     */
    public static Connection getConn(String dsCode) throws Exception {
        log.debug("currentTransStatus[{}] ", transStatus.get());
        if (null == getTransStatus()) {
            throw new Exception("当前操作不在数据库事务中,请使用Service.call进行数据库操作！");
        }

        int currentPropagation = getCurrentPropagation();
        log.debug("currentPropagation[{}] ", currentPropagation);

        String currentTransId = getCurrentTransId();
        log.debug("currentTransId[{}] ", currentTransId);

        if (null == dsCode) {
            dsCode = Constants.DEFAULT_DATASOURCE;
        }

        Connection conn = null;
        Map<String, Map<String, Connection>> connCache = connCtx.get();// {currenttransId,{dsCode,Connection}}
        if (null == connCache)// 事务Cache
        {
            log.debug("init TransStatus[{}] connCache.", getTransStatus());
            connCache = Collections.synchronizedMap(new HashMap<String, Map<String, Connection>>());
            connCtx.set(connCache);

            log.debug("init currentTransId[{}] connMap", currentTransId);
            Map<String, Connection> connMap = Collections.synchronizedMap(new HashMap<String, Connection>());
            connCache.put(currentTransId, connMap);

            log.debug("init dsCode[{}] Conn ", dsCode);
            conn = DsUtils.getDataSource(dsCode).getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connMap.put(dsCode, conn);
        } else {
            Map<String, Connection> connMap = connCache.get(currentTransId);
            if (null == connMap) {
                log.debug("init currentTransId[{}] connMap", currentTransId);
                connMap = Collections.synchronizedMap(new HashMap<String, Connection>());
                connCache.put(currentTransId, connMap);

                log.debug("init dsCode[{}] Conn", dsCode);
                conn = DsUtils.getDataSource(dsCode).getConnection();
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                connMap.put(dsCode, conn);
            } else {
                conn = connMap.get(dsCode);
                if (null == conn) {
                    log.debug("init dsCode[{}] conn ", dsCode);
                    conn = DsUtils.getDataSource(dsCode).getConnection();
                    conn.setAutoCommit(false);
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    connMap.put(dsCode, conn);
                }
            }
        }
        log.debug("getConn dscode[{}] ", dsCode);
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
        return conn;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public static Connection getConn() throws Exception {
        return getConn(null);
    }

    /**
     * @throws SQLException
     * @throws Exception
     * 
     */
    static void commit() {
        String trans = getTransStatus();
        int currentPropagation = getCurrentPropagation();
        if (currentPropagation != 1) {
            return;
        }
        Map<String, Map<String, Connection>> connCache = connCtx.get();
        String currentTransId = getCurrentTransId();
        Map<String, Connection> connMap = connCache.get(currentTransId);
        for (Connection conn : connMap.values()) {
            if (conn != null) {
                try {
                    conn.commit();
                } catch (SQLException e) {
                    log.debug("Trans[{}] commit error!：", trans, e);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.debug("Trans[{}]  conn.close error!", trans, e);
                    }
                }
            }
        }
        clean();
    }

    /**
     * @throws SQLException
     * 
     */
    static void rollback() {
        // 事务传播行为
        String currentTrans = getTransStatus();
        int currentPropagation = getCurrentPropagation();
        String currentTransId = getCurrentTransId();
        if (currentPropagation == Trans.NEW) {
            rollbackByTransId(currentTransId);
        } else {
            Map<String, Map<String, Savepoint>> savepointCache = savePointCtx.get();
            if (null == savepointCache) {
                rollbackByTransId(currentTransId);
            } else {
                String trans = getLastTrans();
                Map<String, Savepoint> savepointMap = savepointCache.get(trans);
                if (null == savepointMap) {
                    rollbackByTransId(currentTransId);
                } else {
                    Map<String, Connection> connMap = connCtx.get().get(currentTransId);
                    for (Map.Entry<String, Connection> entry : connMap.entrySet()) {
                        Connection conn = entry.getValue();
                        String dsCode = entry.getKey();
                        Savepoint savepoint = savepointMap.get(dsCode);
                        if (null == savepoint) {
                            try {
                                conn.rollback();
                            } catch (SQLException e) {
                                log.debug("事务回滚失败：dsCode[{}] ", dsCode, e);
                            } finally {
                                try {
                                    conn.close();
                                } catch (SQLException e) {
                                    log.debug("数据库连接关闭失败：dsCode[{}] ", dsCode, e);
                                }
                            }
                        } else {
                            try {
                                conn.rollback(savepoint);
                            } catch (SQLException e) {
                                log.debug("事务回滚失败：dsCode[{}] ", dsCode, e);
                                e.printStackTrace();
                            }
                        }
                    }
                    // Clean
                    savepointMap = null;
                    savepointCache.remove(trans);
                    // set new
                    String newTransStatus = trans.substring(0, trans.length() - 1);
                    setTransStatus(newTransStatus);
                    log.debug("Trans rollback to trans[{}] !", newTransStatus);
                }
            }
        }
    }

    static void rollbackByTransId(String transId) {
        String trans = getTransStatus();
        Map<String, Connection> connCache = connCtx.get().get(transId);
        for (Map.Entry<String, Connection> entry : connCache.entrySet()) {
            Connection conn = entry.getValue();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    log.debug("trans[{}] rollback error! ", trans, e);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.debug("trans[{}] conn close error! " + trans, e);
                    }
                }
            }
        }
        cleanById(transId);
    }

    /**
     * @throws SQLException
     * 
     */
    private static void cleanById(String transId) {
        Map<String, Connection> connMap = connCtx.get().get(transId);
        connMap = null;
        connCtx.get().remove(transId);
        log.debug("remove trans[{}] ", transId);
    }

    /**
     * @throws SQLException
     * 
     */
    private static void clean() {
        String trans = getTransStatus();
        String currentTransId = getCurrentTransId();
        Map<String, Connection> cacheMap = connCtx.get().get(currentTransId);
        cacheMap = null;
        connCtx.get().remove(currentTransId);
        log.debug("remove trans[{}] ", trans);
    }

}
