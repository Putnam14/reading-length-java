package com.readinglength.archivistws.lib;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariDataSourceFactory {

    private DataSource connectionPool;

    public HikariDataSourceFactory(String connectionName, String databaseName, String userName, String userPass, int timeout) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql:///%s", databaseName));
        config.setUsername(userName);
        config.setPassword(userPass);
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", connectionName);
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        config.setConnectionTimeout(timeout);

        this.connectionPool = new HikariDataSource(config);
    }

    public DataSource getConnectionPool() {
        return this.connectionPool;
    }
}
