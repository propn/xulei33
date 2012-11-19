/**
 * 
 */
package com.propn.golf.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.propn.golf.dao.ds.ConnUtils;
import com.propn.golf.dao.sql.InsertSqlParser;
import com.propn.golf.dao.sql.Po;
import com.propn.golf.dao.sql.SelectSqlParser;
import com.propn.golf.dao.sql.SqlFilter;
import com.propn.golf.dao.sql.SqlMapExe;
import com.propn.golf.dao.sql.SqlUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class DbUtils {

    public static void intsert(Po po) throws Exception {
        String sql = SqlUtils.getInsertSql(po.getClass());
        System.out.println(sql);
        SqlFilter filter = new InsertSqlParser();
        Object[] param = filter.doFilter(sql, po);
        System.out.println(param[0]);
        System.out.println(param[1]);
        Connection conn = ConnUtils.getConnection();
        SqlMapExe.excuteUpdate(conn, (String) param[0], (Object[]) param[1]);
    }

    public static List<Po> qryObjList(Po obj) throws Exception {
        String sql = SqlUtils.getSelectSql(obj.getClass());
        System.out.println(sql);
        SqlFilter filter = new SelectSqlParser();
        Object[] param = filter.doFilter(sql, obj);
        System.out.println(param[0]);
        System.out.println(param[1]);
        Connection conn = ConnUtils.getConnection();
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

    /**
     * @param args
     */
    public static void main(String[] args) {
    }
}
