package com.readinglength.archivistws.dao;

import com.readinglength.archivistws.lib.HikariDataSourceFactory;
import com.readinglength.lib.Book;
import com.readinglength.lib.dao.gcp.SecretsDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
public class BookshelfDao {
    private DataSource connectionPool;

    @Inject
    public BookshelfDao(HikariDataSourceFactory hikariDataSourceFactory) {
        this.connectionPool = hikariDataSourceFactory.getConnectionPool();
    }

    public void insertBook(Book book) throws SQLException {

        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "INSERT INTO books " +
                    "(isbn, title, author, description, pagecount, coverImage, publisher, publishDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(stmt)) {
                insertStmt.setQueryTimeout(10);
                insertStmt.setString(1, book.getIsbn13().toString());
                insertStmt.setString(2, book.getTitle());
                insertStmt.setString(3, book.getAuthor());
                insertStmt.setString(4, book.getDescription());
                insertStmt.setInt(5, book.getPagecount());
                insertStmt.setString(6, book.getCoverImage());
                insertStmt.setString(7, book.getPublisher());
                insertStmt.setDate(8, Date.valueOf(book.getPublishDate()));
                insertStmt.execute();
            }
        }
    }
}
