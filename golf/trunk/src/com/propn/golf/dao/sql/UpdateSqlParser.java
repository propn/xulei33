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
public class UpdateSqlParser extends SqlFilter {

    public String dealOptParam(String sql, Map param) throws Exception {
        List<String> vars = getVars(sql);
        for (String var : vars) {
            if (!containsKey(var, param)) {
                String REXP = ",?\\s*" + var + "\\s*=\\s*[$#]{0,1}(\\{" + var + "\\}|" + var + "),?";
                sql = Pattern.compile(REXP).matcher(sql).replaceAll(",");
                // SET ,
                sql = Pattern.compile("SET\\s*,").matcher(sql).replaceAll("SET ");
                // , WHERE
                sql = Pattern.compile(",\\s*WHERE").matcher(sql).replaceAll(" WHERE");
            }
        }
        return sql;
    }

    public static void main(String[] args) throws Exception {
        String sql = "UPDATE SYSNETPROXYCFG SET PROXYHOSTIP=${PROXYHOSTIP},NAME=${NAME},PROXYPORT=${PROXYPORT},TYPE=${TYPE} WHERE NAME=${name} AND TYPE=${type}";
        Filter parser = new UpdateSqlParser();

        Map parms = new HashMap();

        parms.put("PROXYPORT", "127.0.0.1");
        parms.put("NAME", "name");

        parms.put("name", "127.0.0.1");

        Object[] r = parser.doFilter(sql, parms);
        System.out.println(r[0]);
        Object[] p = (Object[]) r[1];
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + "    ");
        }
        System.out.println();
    }

}
