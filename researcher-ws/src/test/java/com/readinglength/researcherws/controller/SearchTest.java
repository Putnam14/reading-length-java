package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryDao;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

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
        OpenLibraryService openLibraryService = new OpenLibraryService(openLibraryDaoMock, new ObjectMapper());
        instance = new Search(openLibraryService);

        when(openLibraryDaoMock.queryTitle("War and peace")).thenReturn(
                ResponseEntity.accepted().body(Files.readString(Path.of(
                        SearchTest.class.getResource("/test/work.json").getPath()))));

        when(openLibraryDaoMock.queryIsbn(Isbn.of("0061434531"))).thenReturn(
                ResponseEntity.accepted().body(Files.readString(Path.of(
                        SearchTest.class.getResource("/test/edition.json").getPath()))));
    }

    @Test
    void byTitle() {
        ResponseEntity<Isbn> res = instance.byTitle("War and peace");

        assertEquals("0061434531", res.getBody().getIsbn());
    }

    @Test
    void byIsbn() {
        ResponseEntity<Isbn> res = instance.byIsbn(Isbn.of("0061434531"));

        assertEquals("0061434531", res.getBody().getIsbn());
    }
}