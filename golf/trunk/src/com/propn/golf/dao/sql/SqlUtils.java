package com.propn.golf.dao.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.propn.golf.dao.Student;
import com.propn.golf.tools.Cache;
import com.propn.golf.tools.StringUtils;

/**
 * 
 */
public class SqlUtils {
    private static Cache cache = new Cache();

    public static String getInsertSql(Class clz) throws Exception {
        String className = clz.getName();
        if (cache.get(className).isEmpty()) {
            cache.put(className, "C", generalInsertSql(clz));
            cache.put(className, "R", generalSelectSql(clz));
            cache.put(className, "U", generalUpdateSql(clz));
            cache.put(className, "D", generalDeleteSql(clz));
        }
        return cache.get(className, "C").toString();
    }

    public static String getSelectSql(Class clz) throws Exception {
        String className = clz.getName();
        if (cache.get(className).isEmpty()) {
            cache.put(className, "C", generalInsertSql(clz));
            cache.put(className, "R", generalSelectSql(clz));
            cache.put(className, "U", generalUpdateSql(clz));
            cache.put(className, "D", generalDeleteSql(clz));
        }
        return cache.get(className, "R").toString();
    }

    public static String getUpdateSql(Class clz) throws Exception {
        String className = clz.getName();
        if (cache.get(className).isEmpty()) {
            cache.put(className, "C", generalInsertSql(clz));
            cache.put(className, "R", generalSelectSql(clz));
            cache.put(className, "U", generalUpdateSql(clz));
            cache.put(className, "D", generalDeleteSql(clz));
        }
        return cache.get(className, "U").toString();
    }

    public static String getDeleteSql(Class clz) throws Exception {
        String className = clz.getName();
        if (cache.get(className).isEmpty()) {
            cache.put(className, "C", generalInsertSql(clz));
            cache.put(className, "R", generalSelectSql(clz));
            cache.put(className, "U", generalUpdateSql(clz));
            cache.put(className, "D", generalDeleteSql(clz));
        }
        return cache.get(className, "D").toString();
    }

    private static String generalInsertSql(Class clz) throws Exception {
        String tableName = getTableName(clz);
        StringBuffer sqlStr = new StringBuffer("INSERT INTO " + tableName + " (");
        StringBuffer valueStr = new StringBuffer(" VALUES (");
        List<Field> columnFields = getColumnFields(clz);
        if (columnFields != null && columnFields.size() > 0) {
            for (Field field : columnFields) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append(",");
                valueStr.append("${").append(field.getName()).append("}").append(",");
            }
        }
        sqlStr.replace(sqlStr.length() - 1, sqlStr.length(), ")");
        valueStr.replace(valueStr.length() - 1, valueStr.length(), ")");
        return sqlStr.append(valueStr).toString();
    }

    private static String generalDeleteSql(Class clz) throws Exception {
        StringBuffer sqlStr = new StringBuffer("DELETE FROM ");
        sqlStr.append(getTableName(clz));
        sqlStr.append(" WHERE ");
        // Where
        List<Field> ids = getIdFields(clz);
        if (!ids.isEmpty()) {
            for (Field field : ids) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append("=${").append(field.getName()).append("}").append(" AND ");
            }
            sqlStr.replace(sqlStr.length() - 4, sqlStr.length(), "");
        } else {
            throw new Exception(clz.getName() + " 没有主键");
        }
        return sqlStr.toString();
    }

    private static String generalUpdateSql(Class clz) throws Exception {
        String tableName = getTableName(clz);
        StringBuffer sqlStr = new StringBuffer("UPDATE " + tableName + " SET ");

        List<Field> columnFields = getColumnFields(clz);
        if (columnFields != null && columnFields.size() > 0) {
            for (Field field : columnFields) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append("=").append("${").append(field.getName()).append("}").append(",");
            }
        }
        sqlStr.replace(sqlStr.length() - 1, sqlStr.length(), "");
        List<Field> ids = getIdFields(clz);
        if (!ids.isEmpty()) {
            sqlStr.append(" WHERE ");
            for (Field field : ids) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append("=${").append(field.getName()).append("}").append(" AND ");
            }
            sqlStr.replace(sqlStr.length() - 4, sqlStr.length(), "");
        } else {
            throw new Exception(clz.getName() + " 没有主键");
        }
        return sqlStr.toString();
    }

    private static String generalSelectSql(Class clz) throws Exception {
        StringBuffer sqlStr = new StringBuffer("SELECT ");
        List<Field> columnFields = getColumnFields(clz);
        if (columnFields != null && columnFields.size() > 0) {
            for (Field field : columnFields) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append(",");
            }
        }
        sqlStr.replace(sqlStr.length() - 1, sqlStr.length(), " FROM ");
        sqlStr.append(getTableName(clz));
        // Where
        List<Field> ids = getIdFields(clz);
        if (!ids.isEmpty()) {
            sqlStr.append(" WHERE ");
            for (Field field : ids) {
                String column = field.getAnnotation(Column.class).name().toUpperCase();
                if (StringUtils.isBlank(column)) {
                    column = StringUtils.camel4underline(field.getName());
                }
                sqlStr.append(column).append("=${").append(field.getName()).append("}").append(" AND ");
            }
            sqlStr.replace(sqlStr.length() - 4, sqlStr.length(), "");
        }
        return sqlStr.toString();
    }

    private static String getTableName(Class clz) throws Exception {
        String table = null;
        if (clz.isAnnotationPresent(Table.class)) {
            table = ((Table) clz.getAnnotation(Table.class)).name().toUpperCase();
        }
        if (null == table) {
            throw new Exception(clz.getName() + " 没有设定表名");
        }
        return table;
    }

    private static List<Field> getIdFields(Class clz) {
        Map map = RefUtils.getFields(clz);
        Object[] fields = map.values().toArray();

        List<Field> list = new ArrayList<Field>();
        for (int i = 0; i < fields.length; i++) {
            Field field = (Field) fields[i];
            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
                list.add(field);
            }
        }
        return list;
    }

    private static List<Field> getColumnFields(Class clz) {
        List<Field> list = new ArrayList<Field>();
        Map map = RefUtils.getFields(clz);
        Object[] fields = map.values().toArray();
        for (int i = 0; i < fields.length; i++) {
            Field field = (Field) fields[i];
            if (field.isAnnotationPresent(Column.class)) {
                list.add(field);
            }
        }
        return list;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(SqlUtils.getInsertSql(Student.class));
        System.out.println(SqlUtils.getUpdateSql(Student.class));
        System.out.println(SqlUtils.getSelectSql(Student.class));
        System.out.println(SqlUtils.getDeleteSql(Student.class));

        // long s = System.currentTimeMillis();
        // for (int i = 0; i < 100000; i++)
        // {
        //
        // // SqlUtils.generalInsertSql(SysNetProxyCfg.class);
        // // SqlUtils.generalUpdateSql(SysNetProxyCfg.class);
        // // SqlUtils.generalSelectSql(SysNetProxyCfg.class);
        // // SqlUtils.generalDeleteSql(SysNetProxyCfg.class);
        //
        // }
        // System.out.println(System.currentTimeMillis() - s);

    }
}
