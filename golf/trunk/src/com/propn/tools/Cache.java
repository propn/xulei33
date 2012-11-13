package com.propn.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cache<T> {

    // <组件类名或标识, <数据名, 数据值>>
    private ConcurrentMap<String, ConcurrentMap<String, T>> cache = new ConcurrentHashMap<String, ConcurrentMap<String, T>>();

    public Map<String, T> get(String componentName) {
        ConcurrentMap<String, T> value = cache.get(componentName);
        if (value == null){
            return new HashMap<String, T>();
        }
        return new HashMap<String, T>(value);
    }

    public T get(String componentName, String key) {
        if (!cache.containsKey(componentName)) {
            return null;
        }
        return cache.get(componentName).get(key);
    }

    public void put(String componentName, String key, T value) {
        Map<String, T> componentData = cache.get(componentName);
        if (null == componentData) {
            cache.putIfAbsent(componentName, new ConcurrentHashMap<String, T>());
            componentData = cache.get(componentName);
        }
        componentData.put(key, value);
    }

    public void remove(String componentName, String key) {
        if (!cache.containsKey(componentName)) {
            return;
        }
        cache.get(componentName).remove(key);
    }

}
