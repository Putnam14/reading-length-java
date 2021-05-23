package com.readinglength.researcherws.dao.google;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.dao.gcp.SecretsDao;
import com.readinglength.lib.ws.RestClient;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleBooksServiceTest {
    private GoogleBooksDao dao;
    private GoogleBooksService instance;

    @BeforeEach
    void setUp() {
        dao = mock(GoogleBooksDao.class);
        instance = new GoogleBooksService(dao);
    }

    @Test
    void queryTitle() throws IOException, BookNotFoundException {
        when(dao.queryTitle("invisible man")).thenReturn(Files.readString(Path.of(
                this.getClass().getResource("/json/googleBooksResponse.json").getPath())));

        Book book = instance.queryTitle("invisible man");

        assertEquals("1473216842", book.getIsbn10().toString());

    }

    //@Test
    void helper() throws Exception {
        URL baseUrl = new URL("https://www.googleapis.com/books/v1/");
        String key = SecretsDao.getSecret("GOOGLE_BOOKS_API_KEY");
        GoogleBooksDao instance = new GoogleBooksDao(new RestClient(), baseUrl, key);
        String result = instance.queryIsbn(Isbn.of("0307279464"));
        assertNotNull(result);
    }
}