/**
 * 
 */
package com.propn.golf.dao.sql;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.propn.golf.dao.PoUtils;
import com.propn.golf.tools.JsonUtils;
import com.propn.golf.tools.RefUtils;
import com.propn.golf.tools.XmlUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public abstract class Po implements Serializable, Cloneable {

    // 属性
    public Object get(String fieldName) throws Exception {
        Object value = RefUtils.getFieldValue(this, fieldName);
        return value;
    }

    public void set(String fieldName, Object value) throws Exception {
        RefUtils.setFieldValue(this, fieldName, value);
    }

    public void set(Map<String, Object> map) throws Exception {
        Set<String> fieldNames = map.keySet();
        for (String fieldName : fieldNames) {
            RefUtils.setFieldValue(this, fieldName, map.get(fieldName));
        }
    }

    Map toMap() throws Exception {
        Map<String, Object> map = new HashMap();
        Map<String, Field> fields = RefUtils.getFields(this.getClass());
        for (Iterator it = fields.values().iterator(); it.hasNext();) {
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

    // 序列化工具
    public String toJson() {
        return JsonUtils.toJson(this, this.getClass());
    }

    public String toXml() throws JAXBException, IOException, ClassNotFoundException {
        return XmlUtils.toXml(this);
    }

    // 数据库操作CRUD
    public void save() throws Exception {
        PoUtils.intsert(this);
    }

    public <T> T getById(Object id) {
        // 校验主键
        return null;
    }

    public <T> T getOne() throws Exception {
        List<T> pos = qryList();
        return null != pos && pos.size() > 0 ? pos.get(0) : null;
    }

    /**
     * 模版equel查询
     * 
     * @param <T>
     * 
     * @param <T>
     * 
     * @return
     * @throws Exception
     */
    public <T> List<T> qryList() throws Exception {
        return PoUtils.qryPoList(this);
    }

    /**
     * 动态查询
     * 
     * @param <T>
     * 
     * @param qryCode 配置Sql编码
     * @param param 模版入参
     * @return
     */
    public <T> List<T> query(String qryCode, Object param) {
        return null;
    }

    public int update() throws Exception {
        return PoUtils.update(this);
    }

    public int delete() throws Exception {
        return PoUtils.delete(this);
    }

}
