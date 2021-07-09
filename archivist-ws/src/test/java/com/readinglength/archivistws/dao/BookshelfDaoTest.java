package com.readinglength.archivistws.dao;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn13;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class BookshelfDaoTest {
    private static Connection CONNECTION;
    private static DataSource DATA_SOURCE;
    private BookshelfDao instance;
    private PreparedStatement preparedStatement;

    @BeforeAll
    static void setup() throws SQLException {
        DATA_SOURCE = mock(DataSource.class);
        CONNECTION = mock(Connection.class);
        when(DATA_SOURCE.getConnection()).thenReturn(CONNECTION);
    }

    @BeforeEach
    void setInstance() throws SQLException {
        instance = new BookshelfDao(DATA_SOURCE);
        preparedStatement = mock(PreparedStatement.class);
        when(CONNECTION.prepareStatement(any())).thenReturn(preparedStatement);
    }

    @Test
    void insertBook() throws SQLException {
        Book book = new Book.Builder()
                .withIsbn13(new Isbn13("978-1-68051-004-1"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withPublisher("Mountaineers Books")
                .withPublishDate("2017-10-05")
                .withPagecount(624)
                .build();

        instance.insertBook(book);

        verify(preparedStatement).setString(1, book.getIsbn13().toString());
        verify(preparedStatement).setString(2, book.getTitle());
        verify(preparedStatement).setString(3, book.getAuthor());
        verify(preparedStatement).setString(4, book.getDescription());
        verify(preparedStatement).setObject(5, book.getPagecount(), Types.INTEGER);
        verify(preparedStatement).setString(6, book.getCoverImage());
        verify(preparedStatement).setString(7, book.getPublisher());
        verify(preparedStatement).setDate(8, Date.valueOf(book.getPublishDate()));
        verify(preparedStatement).execute();
    }
}