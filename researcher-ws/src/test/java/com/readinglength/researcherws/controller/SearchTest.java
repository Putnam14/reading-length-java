package com.readinglength.researcherws.controller;

import com.amazon.paapi5.v1.ExternalIds;
import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.ItemInfo;
import com.amazon.paapi5.v1.MultiValuedAttribute;
import com.amazon.paapi5.v1.SearchItemsResource;
import com.amazon.paapi5.v1.SearchItemsResponse;
import com.amazon.paapi5.v1.SearchResult;
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
import java.util.List;

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

        when(openLibraryDaoMock.queryTitle("War and peace")).thenReturn(
                Files.readString(Path.of(
                        SearchTest.class.getResource("/test/work.json").getPath())));


        Item result = new Item().itemInfo(new ItemInfo().externalIds(new ExternalIds().isBNs(new MultiValuedAttribute().addDisplayValuesItem("0061434531"))));

        when(amazonDao.queryTitle("War and peace", List.of(
                    SearchItemsResource.ITEMINFO_EXTERNALIDS,
                    SearchItemsResource.ITEMINFO_TITLE,
                    SearchItemsResource.ITEMINFO_BYLINEINFO,
                    SearchItemsResource.ITEMINFO_CONTENTINFO)))
                .thenReturn(new SearchItemsResponse().searchResult(new SearchResult().items(List.of(result))));

        when(openLibraryDaoMock.queryIsbn(Isbn.of("0061434531"))).thenReturn(
                Files.readString(Path.of(
                        SearchTest.class.getResource("/test/edition.json").getPath())));
    }

    @Test
    void byTitle() {
        Book res = instance.byTitle("War and peace").block();

        assertEquals("0061434531", res.getIsbn10());
    }

    @Test
    void byIsbn() {
        Book res = instance.byIsbn("0061434531").block();

        assertEquals("0061434531", res.getIsbn10());
    }
}