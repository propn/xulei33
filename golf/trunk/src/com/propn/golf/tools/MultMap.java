/**
 * 
 */
package com.propn.golf.tools;

/**
 * @author Administrator
 *
 */

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MultMap<T, V> extends HashMap<T, List<V>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public List<V> put(T key, V value) {
        /* 判断该建是否已经存在吗,如果不存在 则放入一个新的Vector对象 */
        if (!super.containsKey(key)) {
            super.put(key, new Vector<V>());
        }
        /* 这里获取 key对应的List */
        List<V> list = super.get(key);
        /* 将当前值，放入到 key对应的List中 */
        list.add(value);
        /* 返回当前 key对于的List对象 */
        return super.get(list);
    }

    @Override
    public List<V> get(Object key) {
        return super.get(key);
    }

    public static void main(String[] args) {
        MultMap<String, String> map = new MultMap<String, String>();
        map.put("1", "2");
        map.put("1", "3");
        map.put("1", "4");
        map.put("1", "5");
        map.put("2", "7");
        map.put("3", "4");
        map.put("2", "3");
        map.put("3", "2");
        System.out.println(map);
    }
}
