/**
 * 
 */
package com.propn.golf.dao;

import java.sql.Connection;

import com.propn.golf.dao.ds.ConnUtils;
import com.propn.golf.dao.sql.InsertSqlParser;
import com.propn.golf.dao.sql.Po;
import com.propn.golf.dao.sql.SqlFilter;
import com.propn.golf.dao.sql.SqlMapExe;
import com.propn.golf.dao.sql.SqlUtils;

/**
 * @author Administrator
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

    /**
     * @param args
     */
    public static void main(String[] args) {
    }
}
