package com.readinglength.archivistws.lib;

import com.readinglength.lib.dao.gcp.SecretsDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Singleton;
import java.util.Properties;

@Singleton
public class HikariDataSourceFactory {

    private static final String CONNECTION_NAME = System.getenv("MYSQL_CONNECTION_NAME");
    private static final String DB_NAME = System.getenv("MYSQL_DB");
    private static final String DB_USER = System.getenv("MYSQL_USER");
    private static final String DB_PASS_SECRET = System.getenv("MYSQL_PASS_SECRET");

    private HikariDataSource connectionPool;


    HikariDataSourceFactory() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        // Set up URL parameters
        String jdbcURL = String.format("jdbc:mysql:///%s", DB_NAME);
        Properties connProps = new Properties();
        connProps.setProperty("user", DB_USER);
        connProps.setProperty("password", SecretsDao.getSecret(DB_PASS_SECRET));
        connProps.setProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        connProps.setProperty("cloudSqlInstance", CONNECTION_NAME);

        // Initialize connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcURL);
        config.setDataSourceProperties(connProps);
        config.setConnectionTimeout(10000); // 10s

        this.connectionPool = new HikariDataSource(config);
    }

    public HikariDataSource getConnectionPool() {
        return this.connectionPool;
    }
}
