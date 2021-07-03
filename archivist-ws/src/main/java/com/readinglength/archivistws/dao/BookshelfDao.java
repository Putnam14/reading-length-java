package com.readinglength.archivistws.dao;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.lib.Isbn13;
import com.readinglength.lib.Wordcount;

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

    public void insertWordcount(Wordcount wordcount) throws SQLException {
        if (queryForWordcount(wordcount.getIsbn()) != null) {
            upsertWordcount(wordcount);
        } else {
            try (Connection conn = connectionPool.getConnection()) {
                String stmt = "INSERT INTO wordcounts " +
                        "(isbn, userId, wordcount, wordcountType) " +
                        "VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(stmt)) {
                    insertStmt.setQueryTimeout(10);
                    insertStmt.setString(1, Isbn13.convert(wordcount.getIsbn()).toString());
                    insertStmt.setInt(2, wordcount.getUserId());
                    insertStmt.setInt(3, wordcount.getWords());
                    insertStmt.setInt(4, wordcount.getType().getId());
                    insertStmt.execute();
                }
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

    public Wordcount queryForWordcount(Isbn isbn) throws SQLException {
        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "SELECT userId, wordcount, wordcountType FROM wordcounts WHERE isbn = ? LIMIT 1";
            try (PreparedStatement queryStmt = conn.prepareStatement(stmt)) {
                queryStmt.setQueryTimeout(10);
                queryStmt.setString(1, Isbn13.convert(isbn).toString());
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    return new Wordcount(isbn, rs.getInt("wordcount"), rs.getInt("userId"), Wordcount.WordcountType.byId(rs.getInt("wordcountType")));
                }
            }
        }
        return null;
    }

    private void upsertWordcount(Wordcount wordcount) throws SQLException {
        try (Connection conn = connectionPool.getConnection()) {
            String stmt = "UPDATE wordcounts " +
                    "SET userId = ?, " +
                    "wordcount = ?, " +
                    "wordcountType = ? " +
                    "WHERE isbn = ?";
            try (PreparedStatement insertStmt = conn.prepareStatement(stmt)) {
                insertStmt.setQueryTimeout(10);
                insertStmt.setInt(1, wordcount.getUserId());
                insertStmt.setInt(2, wordcount.getWords());
                insertStmt.setInt(3, wordcount.getType().getId());
                insertStmt.setString(4, Isbn13.convert(wordcount.getIsbn()).toString());
                insertStmt.execute();
            }
        }
    }
}
