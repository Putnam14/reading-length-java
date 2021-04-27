package com.readinglength.archivistws;

import com.readinglength.archivistws.dao.BookshelfDao;
import com.readinglength.archivistws.lib.HikariDataSourceFactory;
import com.readinglength.lib.dao.gcp.SecretsDao;
import dagger.Provides;
import dagger.Module;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Module
public class ArchivistModule {
    @Provides
    @Singleton
    @Named("DS_BOOKS")
    public DataSource provideDataSource() {
        String connectionName = System.getenv("MYSQL_CONNECTION_NAME");
        String databaseName = System.getenv("MYSQL_DB");
        String userName = System.getenv("MYSQL_USER");
        String userPassSecret = System.getenv("MYSQL_PASS_SECRET");
        String userPass = null;
        try {
            userPass = SecretsDao.getSecret(userPassSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HikariDataSourceFactory(connectionName, databaseName, userName, userPass, 10000).getConnectionPool();
    }

}
