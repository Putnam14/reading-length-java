package com.readinglength.archivistws.dao;

import com.readinglength.archivistws.lib.HikariDataSourceFactory;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn13;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class BookshelfDao {
    private static Logger LOG = LoggerFactory.getLogger(BookshelfDao.class);
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

    public Book queryBook(Isbn isbn) throws SQLException {
        LOG.info(String.format("Querying db for ISBN %s", isbn));
        try (Connection conn = connectionPool.getConnection()) {
            LOG.info("Got connection");
            String stmt = "SELECT * FROM books WHERE isbn = ?";
            try (PreparedStatement queryStmt = conn.prepareStatement(stmt)) {
                queryStmt.setQueryTimeout(10);
                queryStmt.setString(1, Isbn13.convert(isbn).toString());
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    LOG.info("Data found in db");
                    LOG.info(rs.toString());
                    Book result = new Book();
                    result.setIsbn13(new Isbn13(rs.getString("isbn")));
                    result.setTitle(rs.getString("title"));
                    result.setAuthor(rs.getString("author"));
                    result.setDescription(rs.getString("description"));
                    result.setPagecount(rs.getInt("pagecount"));
                    result.setCoverImage(rs.getString("coverImage"));
                    result.setPublisher(rs.getString("publisher"));
                    result.setPublishDate(rs.getDate("publishDate").toString());
                    return result;
                }
            }
        }
        return null;
    }
}
