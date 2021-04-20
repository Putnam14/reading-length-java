package com.readinglength.archivistws.lib;

import com.readinglength.lib.dao.gcp.SecretsDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class HikariDataSourceFactory {

    private static final String CONNECTION_NAME = System.getenv("MYSQL_CONNECTION_NAME");
    private static final String DB_NAME = System.getenv("MYSQL_DB");
    private static final String DB_USER = System.getenv("MYSQL_USER");
    private static final String DB_PASS_SECRET = System.getenv("MYSQL_PASS_SECRET");

    private DataSource connectionPool;


    public HikariDataSourceFactory() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
        config.setUsername(DB_USER);
        try {
            config.setPassword(SecretsDao.getSecret(DB_PASS_SECRET));
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", CONNECTION_NAME);
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        config.setConnectionTimeout(10000); // 10s

        this.connectionPool = new HikariDataSource(config);
    }

    public DataSource getConnectionPool() {
        return this.connectionPool;
    }
}
