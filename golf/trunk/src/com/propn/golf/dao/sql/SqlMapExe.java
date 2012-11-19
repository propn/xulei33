package com.propn.golf.dao.sql;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.propn.golf.tools.StringUtils;

/**
 * <pre>
 * 功能描述：Sql工具
 * 1.
 * 2.
 * </pre>
 * 
 * @author Thunder.Hsu
 * @Create Date : 2012-10-12
 * @Version : 1.0
 */
public class SqlMapExe {

    /**
     * 查询返回一个Map
     * 
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public static Map<String, Object> qryMap(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> rstMap = null;
        try {
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (null != params && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    setParam(stmt, i + 1, params[i]);
                }
            }
            rs = stmt.executeQuery();
            rstMap = row2Map(rs, getMetaData(rs));
        } catch (SQLException e) {
            throw e;
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return rstMap;
    }

    /**
     * 查询返回List<Map<String, Object>>结构
     * 
     * @param sql
     * @param param
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> qryMapList(Connection conn, String sql, Object[] param) throws SQLException {
        List<Map<String, Object>> rstList = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (null != param && param.length > 0) {
                for (int i = 0; i < param.length; i++) {
                    setParam(stmt, i + 1, param[i]);
                }
            }
            rs = stmt.executeQuery();
            rstList = resultSet2ListMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return rstList;
    }

    public static String qryString(Connection conn, String sql) throws SQLException {
        String rst = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
            while (rs.first()) {
                rst = String.valueOf(rs.getObject(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return rst;
    }

    public static Object qryOne(Connection conn, String sql, Object[] params) throws Exception {
        Object rst = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (null != params && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    setParam(stmt, i + 1, params[i]);
                }
            }
            rs = stmt.executeQuery(sql);
            rst = rs.getObject(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return rst;
    }

    public static int excuteUpdate(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                setParam(stmt, i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeStatement(stmt);
        }
    }

    public static void excuteBatchUpdate(Connection conn, String sql, List<Object[]> args) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (Iterator<Object[]> it = args.iterator(); it.hasNext();) {
                Object[] params = it.next();
                for (int i = 0; i < params.length; i++) {
                    setParam(stmt, i + 1, params[i]);
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeStatement(stmt);
        }
    }

    public static String getSeqNextVal(Connection conn, String seqName) throws SQLException {
        return qryString(conn, "select " + seqName + ".nextval from dual");
    }

    /**
     * 调用数据库函数返回一个VARCHAR
     * 
     * @param name 数据库函数名
     * @param inParams 入参数组
     * @return
     * @throws SQLException
     */
    public static String callDbFunc(Connection conn, String name, String[] inParams) throws SQLException {
        CallableStatement cstmt = null;
        String ret = null;
        try {
            String sql = "{ ? = call " + name + "}"; // oracle调用方式
            cstmt = conn.prepareCall(sql);
            cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            if (inParams != null) {
                for (int i = 0; i < inParams.length; i++) {
                    cstmt.setString(i + 2, inParams[i]);
                }
            }
            cstmt.execute();
            ret = cstmt.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return ret;
    }

    /**
     * Oracle存储过程调用
     * 
     * @param jndiName
     * @param procName
     * @param inParams
     * @param outTypes 返回值类型
     * @return
     * @throws Exception
     */
    public static List callProc(Connection conn, String procName, List inParams, int[] outParamTypes)
            throws SQLException {
        CallableStatement cstmt = null;
        int outParamIndex = (inParams == null ? 0 : inParams.size());
        List result = (outParamTypes == null || outParamTypes.length == 0) ? null : new ArrayList();
        String callSQL = "";
        try {
            StringBuffer paramStr = new StringBuffer();
            // 设置绑定参数位置 ?
            wrapProcParam(paramStr, inParams);
            paramStr.append(",");
            wrapProcParam(paramStr, outParamTypes);
            callSQL = "{ call " + procName.toUpperCase() + "(" + paramStr.toString() + ")}";
            cstmt = conn.prepareCall(callSQL);
            // 设置输入参数
            if (inParams != null && !inParams.isEmpty()) {
                for (int i = 0; i < inParams.size(); i++) {
                    Object param = inParams.get(i);
                    setParam(cstmt, i + 1, param);
                }
            }
            // 设置输出参数
            if (outParamTypes != null && outParamTypes.length > 0) {
                for (int i = 0, j = outParamTypes.length; i < j; i++) {
                    cstmt.registerOutParameter(outParamIndex + i + 1, outParamTypes[i]);
                }
            }
            cstmt.execute();
            // 组装输出结果
            if (result != null) {
                for (int i = 0, j = outParamTypes.length; i < j; i++) {
                    Object o = getObject(cstmt, outParamIndex + i + 1, outParamTypes[i]);
                    result.add(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cstmt.close();
        }
        return result;
    }

    private static void setParam(PreparedStatement stmt, int index, Object param) throws SQLException {
        if (param instanceof String) {
            stmt.setString(index, (String) param);
        } else if (param instanceof Boolean) {
            stmt.setBoolean(index, ((Boolean) param).booleanValue());
        } else if (param instanceof Byte) {
            stmt.setByte(index, ((Byte) param).byteValue());
        } else if (param instanceof Short) {
            stmt.setShort(index, ((Short) param).shortValue());
        } else if (param instanceof Integer) {
            stmt.setInt(index, ((Integer) param).intValue());
        } else if (param instanceof Long) {
            stmt.setLong(index, ((Long) param).longValue());
        } else if (param instanceof Float) {
            stmt.setFloat(index, ((Float) param).floatValue());
        } else if (param instanceof Double) {
            stmt.setDouble(index, ((Double) param).doubleValue());
        } else if (param instanceof BigDecimal) {
            stmt.setBigDecimal(index, (BigDecimal) param);
        } else if (param instanceof Date) {
            stmt.setDate(index, (Date) param);
        } else if (param instanceof Time) {
            stmt.setTime(index, (Time) param);
        } else if (param instanceof Timestamp) {
            stmt.setTimestamp(index, (Timestamp) param);
        } else {
            stmt.setObject(index, param);
        }
    }

    private static Object getObject(CallableStatement s, int i, int dataType) throws SQLException {
        if (dataType == Types.DATE)
            return s.getDate(i);
        if (dataType == Types.TIMESTAMP)
            return s.getTimestamp(i);
        if (dataType == Types.INTEGER)
            return s.getInt(i);
        if (dataType == Types.DOUBLE)
            return s.getDouble(i);
        return s.getString(i);
    }

    private static void wrapProcParam(StringBuffer sb, List params) {
        if (params == null || params.isEmpty())
            return;
        wrapProcParam(sb, params.size());
    }

    private static void wrapProcParam(StringBuffer sb, int[] params) {
        if (params == null || params.length == 0)
            return;
        wrapProcParam(sb, params.length);
    }

    private static void wrapProcParam(StringBuffer sb, int paramSize) {
        for (int i = 0, j = paramSize; i < j; i++) {
            if (i == j - 1) {
                sb.append("?");
            } else {
                sb.append("?,");
            }
        }
    }

    private static Map<String, Integer> getMetaData(ResultSet rs) throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int column = 0; column < columnCount; column++) {
            String columnName = metaData.getColumnLabel(column + 1);
            int colunmType = metaData.getColumnType(column + 1);
            map.put(columnName, colunmType);
        }
        return map;
    }

    /**
     * resultSet2ListMap
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> resultSet2ListMap(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }
        List<Map<String, Object>> rstList = new ArrayList<Map<String, Object>>();
        Map<String, Integer> metaData = null;
        while (rs.next()) {
            if (rs.isFirst()) {
                metaData = getMetaData(rs);
            }
            rstList.add(row2Map(rs, metaData));
        }
        return rstList;
    }

    private static Map<String, Object> row2Map(ResultSet rs, Map<String, Integer> metaData) throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String columnName : metaData.keySet()) {
            int columnType = metaData.get(columnName);
            Object object = rs.getObject(columnName);
            map.put(StringUtils.underline4camel(columnName), convert(object, columnType));
        }
        return map;
    }

    private static Object convert(Object object, int columnType) {
        Object rst = null;
        if (object == null) {
            return rst;
        }
        switch (columnType) {
        case java.sql.Types.VARCHAR:
            rst = String.valueOf(object);
            break;
        case java.sql.Types.DATE:
            rst = String.valueOf(object);
            break;
        case java.sql.Types.TIMESTAMP:
            rst = String.valueOf(object);
            break;
        case java.sql.Types.TIME:
            rst = String.valueOf(object);
            break;
        case java.sql.Types.CLOB:
            try {
                if (object != null) {
                    Clob clob = (Clob) object;
                    long length = clob.length();
                    rst = clob.getSubString(1L, (int) length);
                }
            } catch (Exception e) {
            }
            break;
        case java.sql.Types.BLOB:
            rst = null;
            break;
        default:
            rst = object;
            break;
        }
        return rst;
    }

    private static void closeResultSet(ResultSet result) throws SQLException {
        if (result != null) {
            result.close();
        }
    }

    private static void closeStatement(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    private static void closeConnection(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

}
