/**
 * 
 */
package com.propn.golf.dao.sql;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.propn.golf.dao.DbUtils;
import com.propn.golf.tools.JsonUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Po implements Serializable {

    // 属性设置
    public Object get(String fieldName) throws Exception {
        Object value = RefUtils.getFieldValue(this, fieldName);
        return value;
    }

    public void set(String fieldName, Object value) throws Exception {
        RefUtils.setFieldValue(this, fieldName, value);
    }

    Map toMap() throws Exception {
        Map<String, Object> map = new HashMap();
        Map<String, Field> fields = RefUtils.getFields(this.getClass());
        for (Iterator it = fields.keySet().iterator(); it.hasNext();) {
            Field field = (Field) it.next();
            Object v = field.get(this);
            if (v instanceof Po) {
                map.put(field.getName(), ((Po) v).toMap());
            } else {
                map.put(field.getName(), v);
            }
        }
        return map;
    }

    public Map toColumnMap() throws Exception {
        Map<String, Object> map = new HashMap();
        Map<String, Field> fields = RefUtils.getFields(this.getClass());
        for (Iterator it = fields.values().iterator(); it.hasNext();) {
            Field field = (Field) it.next();
            Object v = field.get(this);
            String column = field.getAnnotation(Column.class).name();
            if (v instanceof Po) {
                map.put(null == column ? field.getName().toUpperCase() : column.toUpperCase(), ((Po) v).toColumnMap());
            } else {
                map.put(null == column ? field.getName().toUpperCase() : column.toUpperCase(), v);
            }
        }
        return map;
    }

    // 序列化工具
    String toJson() {
        return JsonUtils.toJson(this, this.getClass());
    }

    //
    String toXml() {
        return null;
    }

    // 数据库操作CRUD
    public void save() throws Exception {
        System.out.println("currentThread :" + Thread.currentThread().getId());
        DbUtils.intsert(this);
    }

    Po getById(Object id) {
        // 校验主键
        return null;
    }

    Po getOne() {
        List<Po> pos = getList();
        return null != pos && pos.size() > 0 ? pos.get(0) : null;
    }

    /**
     * 模版equel查询
     * 
     * @return
     */
    List<Po> getList() {
        return null;
    }

    /**
     * 动态查询
     * 
     * @param qryCode 配置Sql编码
     * @param param 模版入参
     * @return
     */
    List<Po> query(String qryCode, Object param) {

        return null;
    }

    void update() {
        // DbUtils.getInstance().update(this);
    }

    void delete() {
        // DbUtils.getInstance().delete(this);
    }

}
