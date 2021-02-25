package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleBooksServiceTest {
    private GoogleBooksDao dao;
    private GoogleBooksService instance;

    @BeforeEach
    void setUp() {
        dao = mock(GoogleBooksDao.class);
        instance = new GoogleBooksService(dao, new ObjectMapper());
    }

    @Test
    void queryTitle() throws IOException, BookNotFoundException {
        when(dao.queryTitle("invisible man")).thenReturn(Files.readString(Path.of(
                this.getClass().getResource("/test/googleBooksResponse.json").getPath())));
        Book book = new Book();

        instance.queryTitle("invisible man", book);

        assertEquals("1473216842", book.getIsbn10().getIsbn());

    }
}