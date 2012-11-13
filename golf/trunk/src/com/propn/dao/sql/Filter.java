package com.propn.dao.sql;

public interface Filter {
    public Object[] doFilter(String sql, final Object param) throws Exception;
}
