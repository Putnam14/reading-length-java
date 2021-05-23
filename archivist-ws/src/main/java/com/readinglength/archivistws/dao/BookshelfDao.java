package com.readinglength.archivistws.dao;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.lib.Isbn13;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookshelfDao {
    private DataSource connectionPool;

    @Inject
    BookshelfDao(@Named("DS_BOOKS") DataSource connectionPool) {
        this.connectionPool = connectionPool;
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
        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "SELECT * FROM books WHERE isbn = ?";
            try (PreparedStatement queryStmt = conn.prepareStatement(stmt)) {
                queryStmt.setQueryTimeout(10);
                queryStmt.setString(1, Isbn13.convert(isbn).toString());
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    return new Book.Builder()
                            .withIsbn10(Isbn10.convert(isbn))
                            .withTitle(rs.getString("title"))
                            .withAuthor(rs.getString("author"))
                            .withDescription(rs.getString("description"))
                            .withPagecount(rs.getInt("pagecount"))
                            .withCoverImage(rs.getString("coverImage"))
                            .withPublisher(rs.getString("publisher"))
                            .withPublishDate((rs.getDate("publishDate").toString()))
                            .build();
                }
            }
        }
        return null;
    }

    public Isbn13 queryIsbn(String title) throws SQLException {
        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "SELECT isbn FROM books WHERE title = ?";
            try (PreparedStatement queryStmt = conn.prepareStatement(stmt)) {
                queryStmt.setQueryTimeout(10);
                queryStmt.setString(1, title);
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    return new Isbn13(rs.getString("isbn"));
                }
            }
        }
        return null;
    }

    public boolean queryForIsbn(Isbn isbn) throws SQLException {
        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "SELECT isbn FROM books WHERE isbn = ? LIMIT 1";
            try (PreparedStatement queryStmt = conn.prepareStatement(stmt)) {
                queryStmt.setQueryTimeout(10);
                queryStmt.setString(1, Isbn13.convert(isbn).toString());
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }
}
