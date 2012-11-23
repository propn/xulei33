/**
 * 
 */
package com.propn.golf.tools;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.mvc.GolfFilter;

/**
 * @author Thunder.Hsu
 * 
 */
public class ConvertUtils {

    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);

    public static Object convert(Object obj, Class dist) throws Exception {
        if (null == obj) {
            return null;
        }
        if (obj.getClass().equals(dist)) {
            return obj;
        }
        log.debug("Convert Object Type: " + obj.getClass().getName() + " " + dist.getName());
        return convert(obj.getClass(), obj, dist);
    }

    private static Object convert(Class<? extends Object> src, Object obj, Class dist) throws Exception {
        if (obj instanceof BigDecimal) {
            return convert(dist, (BigDecimal) obj);
        }
        if (obj instanceof Vector) {
            return convert(dist, (Vector) obj);
        }
        throw new Exception(obj.getClass() + " 转 " + dist + "未实现!");
    }

    // BigDecimal
    private static Object convert(Class distClass, BigDecimal obj) throws Exception {
        if (obj.getClass().equals(BigDecimal.class) && distClass.equals(int.class)) {
            return ((BigDecimal) obj).intValue();
        }
        if (distClass == String.class) {
            return String.valueOf(obj);
        }
        throw new Exception(obj.getClass() + " 转 " + distClass + "未实现!");
    }

    // Vector
    private static Object convert(Class distClass, Vector v) throws Exception {

        if (distClass == String.class) {
            StringBuffer sb = new StringBuffer();
            for (Iterator it = v.iterator(); it.hasNext();) {
                sb.append(convert(it.next(), String.class)).append(",");
            }
            return sb.substring(0, sb.length() - 1).toString();
        }

        if (distClass == int.class) {
            if (v.size() == 1) {
                return Integer.valueOf((String) v.firstElement());
            }
        }
        throw new Exception(v.getClass() + " 转 " + distClass + "未实现!");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }
}
