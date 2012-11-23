/**
 * 
 */
package com.propn.golf.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.propn.golf.dao.sql.InsertSqlParser;
import com.propn.golf.dao.sql.Po;
import com.propn.golf.dao.sql.SelectSqlParser;
import com.propn.golf.dao.sql.SqlFilter;
import com.propn.golf.dao.sql.SqlMapExe;
import com.propn.golf.dao.sql.SqlUtils;
import com.propn.golf.dao.sql.UpdateSqlParser;
import com.propn.golf.dao.trans.ConnUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class PoUtils {

    public static void intsert(Po po) throws Exception {
        String sql = SqlUtils.getInsertSql(po.getClass());
        SqlFilter filter = new InsertSqlParser();
        Object[] param = filter.doFilter(sql, po);
        Connection conn = ConnUtils.getConn();
        SqlMapExe.excuteUpdate(conn, (String) param[0], (Object[]) param[1]);
    }

    public static List<Po> qryPoList(Po obj) throws Exception {
        String sql = SqlUtils.getSelectSql(obj.getClass());
        SqlFilter filter = new SelectSqlParser();
        Object[] param = filter.doFilter(sql, obj);
        Connection conn = ConnUtils.getConn();
        List<Map<String, Object>> maps = SqlMapExe.qryMapList(conn, (String) param[0], (Object[]) param[1]);
        // 转换结果
        List<Po> rst = new ArrayList<Po>();
        for (Map<String, Object> map : maps) {
            Po po = obj.getClass().newInstance();
            po.set(map);
            rst.add(po);
        }
        return rst;
    }

    public static int update(Po po) throws Exception {
        String sql = SqlUtils.getUpdateSql(po.getClass());
        SqlFilter filter = new UpdateSqlParser();
        Object[] param = filter.doFilter(sql, po);
        Connection conn = ConnUtils.getConn();
        return SqlMapExe.excuteUpdate(conn, (String) param[0], (Object[]) param[1]);
    }

    public static int delete(Po po) throws Exception {
        String sql = SqlUtils.getDeleteSql(po.getClass());
        SqlFilter filter = new UpdateSqlParser();
        Object[] param = filter.doFilter(sql, po);
        Connection conn = ConnUtils.getConn();
        return SqlMapExe.excuteUpdate(conn, (String) param[0], (Object[]) param[1]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }
}
