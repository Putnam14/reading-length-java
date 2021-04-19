package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.amazon.AmazonDao;
import com.readinglength.researcherws.dao.amazon.AmazonService;
import com.readinglength.researcherws.dao.google.GoogleBooksDao;
import com.readinglength.researcherws.dao.google.GoogleBooksService;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryDao;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchTest {
    private static Search instance;

    @BeforeAll
    static void setUp() throws IOException {
        OpenLibraryDao openLibraryDaoMock = mock(OpenLibraryDao.class);
        AmazonDao amazonDao = mock(AmazonDao.class);
        OpenLibraryService openLibraryService = new OpenLibraryService(openLibraryDaoMock, new ObjectMapper());
        AmazonService amazonService = new AmazonService(amazonDao);
        GoogleBooksDao googleBooksDao = mock(GoogleBooksDao.class);
        GoogleBooksService googleBooksService = new GoogleBooksService(googleBooksDao, new ObjectMapper());
        instance = new Search(openLibraryService, amazonService, googleBooksService);

        when(openLibraryDaoMock.queryTitle("War and peace")).thenReturn(loadJson("json/work.json"));
        when(amazonDao.searchItems("War and peace")).thenReturn(loadJson("json/amazonDaoResponse-warAndPeace.json"));
        when(openLibraryDaoMock.queryIsbn(Isbn.of("0061434531"))).thenReturn(loadJson("json/edition.json"));
    }

    @Test
    void byTitle() {
        Book res = instance.byTitle("War and peace").block();

        assertEquals("0061434531", res.getIsbn10().toString());
    }

    @Test
    void byIsbn() {
        Book res = instance.byIsbn("0061434531").block();

        assertEquals("0061434531", res.getIsbn10().toString());
    }

    private static String loadJson(String path) {
        try {
            return Files.readString(Path.of(
                            SearchTest.class.getClassLoader().getResource(path).getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}