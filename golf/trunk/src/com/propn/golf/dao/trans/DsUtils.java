package com.propn.golf.dao.trans;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCPDataSource;
import com.propn.golf.Golf;
import com.propn.golf.tools.XmlUtils;

public class DsUtils {

    private static final Logger log = LoggerFactory.getLogger(DsUtils.class);

    private static Map<String, DataSource> cache = Collections.synchronizedMap(new HashMap<String, DataSource>());
    private static InitialContext ic = null;

    static {
        try {
            ic = new InitialContext();
            // 初始化数据源
            InputStream dsIn = ClassLoader.getSystemResourceAsStream(Golf.DATASOURCE_FILE_NAME);
            if (null == dsIn) {
                dsIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(Golf.DATASOURCE_FILE_NAME);
            }
            Document doc = XmlUtils.load(dsIn);
            Map DataSources = (Map) XmlUtils.doc2Map(doc).get("DataSources");
            Object obj = DataSources.get("DataSource");
            if (obj instanceof Map) {
                Map<String, String> ds = (Map) obj;
                String jndi = ds.get("jndi");
                if (null == jndi || jndi.isEmpty()) {
                    cache.put(ds.get("code"), initDs(ds));
                } else {
                    cache.put(ds.get("code"), (DataSource) ic.lookup(jndi));
                }
            } else if (obj instanceof List) {
                List<Map<String, String>> dss = (List<Map<String, String>>) obj;
                for (Map<String, String> ds : dss) {
                    String jndi = ds.get("jndi");
                    if (null == jndi || jndi.isEmpty()) {
                        cache.put(ds.get("code"), initDs(ds));
                    } else {
                        cache.put(ds.get("code"), (DataSource) ic.lookup(jndi));
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("初始化数据源错误!", e);
            throw e;
        } catch (Exception e) {
            log.error("初始化数据源错误!", e);
            throw new RuntimeException(e);
        }
    }

    private static DataSource initDs(Map<String, String> map) throws RuntimeException {
        try {
            String driverClass = map.get("driverClass");
            String jdbcUrl = map.get("jdbcUrl");
            String username = map.get("username");
            String password = map.get("password");
            String maxConnectionAgeInSeconds = map.get("maxConnectionAgeInSeconds");
            String partitionCount = map.get("partitionCount");
            String maxConnectionsPerPartition = map.get("maxConnectionsPerPartition");
            String minConnectionsPerPartition = map.get("minConnectionsPerPartition");

            Class.forName(driverClass);
            // 连接
            BoneCPDataSource dataSource = new BoneCPDataSource();
            dataSource.setDriverClass(driverClass);
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            // 超时
            dataSource.setMaxConnectionAgeInSeconds(Long.valueOf(maxConnectionAgeInSeconds));
            // 连接数
            dataSource.setPartitionCount(Integer.parseInt(partitionCount));
            dataSource.setMinConnectionsPerPartition(Integer.parseInt(minConnectionsPerPartition));
            dataSource.setMaxConnectionsPerPartition(Integer.parseInt(maxConnectionsPerPartition));
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("初始化数据源失败!", e);
        }
    }

    static DataSource getDataSource(String code) throws Exception {
        DataSource dataSource = cache.get(code);
        return dataSource;
    }
}
