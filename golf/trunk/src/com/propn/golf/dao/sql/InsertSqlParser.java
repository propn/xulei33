/**
 * 
 */
package com.propn.golf.dao.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Thunder.xu
 * 
 */
public class InsertSqlParser extends SqlFilter {

    @Override
    public String dealOptParam(String sql, Map param) throws Exception {
        List<String> vars = getVars(sql);
        for (String var : vars) {
            if (!containsKey(var, param)) {
                String REXP = ",?//s*[$#]{0,1}(\\{" + var + "\\}|" + var + ")//s*,?";
                sql = Pattern.compile(REXP).matcher(sql).replaceAll(",");
                REXP = ",//s*[)]";
                sql = Pattern.compile(REXP).matcher(sql).replaceAll(")");
                REXP = "[(]//s*,";
                sql = Pattern.compile(REXP).matcher(sql).replaceAll("(");
            }
        }
        return sql;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String sql = "INSERT INTO SYSNETPROXYCFG (PROXYHOSTIP,NAME,PROXYPORT,TYPE) VALUES (${PROXYHOSTIP},${NAME},${PROXYPORT},${TYPE})";
        // String REXP="\\$?\\{(\\S*?)\\}";
        // //
        // // REXP = ",?[$#]{0,1}(\\{PROXYHOSTIP\\}|PROXYHOSTIP),?";
        // sql = Pattern.compile(REXP).matcher(sql).replaceAll("?");
        // System.out.println(sql);
        //
        // REXP = ",[)]";
        // str = Pattern.compile(REXP).matcher(str).replaceAll(")");
        // System.out.println(str);
        //
        // REXP = "[(],";
        // str = Pattern.compile(REXP).matcher(str).replaceAll("(");
        // System.out.println(str);
        // REXP = "[,*],]";
        // str = Pattern.compile(REXP).matcher(str).replaceAll("(");
        // System.out.println(str);

        Filter sqlParser = new InsertSqlParser();

        Map parms = new HashMap();

        parms.put("PROXYPORT", "127.0.0.1");
        Object[] r = sqlParser.doFilter(sql, parms);
        System.out.println(r[0]);
        Object[] p = (Object[]) r[1];
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + "    ");
        }
        System.out.println();

        parms.put("PROXYHOSTIP", "127.0.0.1");
        r = sqlParser.doFilter(sql, parms);
        System.out.println(r[0]);
        p = (Object[]) r[1];
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + "    ");
        }
        System.out.println();
        //
        parms.put("NAME", "127.0.0.1");
        r = sqlParser.doFilter(sql, parms);
        System.out.println(r[0]);
        p = (Object[]) r[1];
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + "    ");
        }
        System.out.println();
        //
        parms.put("TYPE", "127.0.0.1");
        r = sqlParser.doFilter(sql, parms);
        System.out.println(r[0]);
        p = (Object[]) r[1];
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + "    ");
        }

    }
}
