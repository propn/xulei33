package com.propn.golf.dao.trans;

import javax.sql.DataSource;

import com.propn.golf.dao.trans.DsUtils;

public class DsUtilsTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        DataSource dataSource = DsUtils.getDataSource("golf");
        dataSource = DsUtils.getDataSource("default");
    }
}
