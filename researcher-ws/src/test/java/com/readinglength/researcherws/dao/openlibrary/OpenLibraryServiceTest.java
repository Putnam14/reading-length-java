package com.readinglength.researcherws.dao.openlibrary;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenLibraryServiceTest {
    private OpenLibraryDao dao;
    private OpenLibraryService instance;

    @BeforeEach
    void setUp() {
        dao = mock(OpenLibraryDao.class);
        instance = new OpenLibraryService(dao);
    }

    @Test
    void queryTitle() throws IOException, BookNotFoundException {
        String keyword = "the push";
        when(dao.queryTitle(keyword)).thenReturn(Files.readString(Path.of(this.getClass().getResource("/json/openLibraryResponse-thePush.json").getPath())));

        List<Isbn> isbns = instance.queryTitle(keyword);

        assertEquals(Isbn.of("9781524778125"), isbns.get(0));

    }

    @Test
    void queryIsbn() throws IOException, BookNotFoundException {
        Isbn isbn = Isbn.of("9781524778125");
        when(dao.queryIsbn(isbn)).thenReturn(Files.readString(Path.of(this.getClass().getResource("/json/openLibraryResponse-thePushIsbn.json").getPath())));

        Book result = instance.queryIsbn(isbn);

        assertEquals("The Push", result.getTitle());
        assertEquals("9781524778125", result.getIsbn13().toString());
        assertEquals("1524778125", result.getIsbn10().toString());
        assertEquals("2017-05-16", result.getPublishDate().toString());
        assertEquals("https://covers.openlibrary.org/b/isbn/9781524778125-L.jpg", result.getCoverImage());
        assertEquals("Penguin Audio", result.getPublisher());
    }
}