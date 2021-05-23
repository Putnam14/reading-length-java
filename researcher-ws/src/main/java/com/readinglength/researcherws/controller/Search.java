package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.dao.amazon.AmazonService;
import com.readinglength.researcherws.dao.google.GoogleBooksService;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import com.readinglength.researcherws.lib.BookNotFoundException;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public
class Search {
    private static Logger LOG = LoggerFactory.getLogger(Search.class);

    private OpenLibraryService openLibraryService;
    private AmazonService amazonService;
    private GoogleBooksService googleBooksService;
    private ObjectMapper objectMapper;

    @Inject
    public Search(OpenLibraryService openLibraryService, AmazonService amazonService, GoogleBooksService googleBooksService) {
        this.openLibraryService = openLibraryService;
        this.amazonService = amazonService;
        this.googleBooksService = googleBooksService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public Handler index = ctx -> ctx.result("Hi");

    public Handler byTitle = ctx -> {
        String title = ctx.queryParam("title");
        long startTime = System.currentTimeMillis();
        Book book = new Book.Builder().build();
        try {
            LOG.info(String.format("Querying OL for %s", title));
            List<Isbn> isbns = openLibraryService.queryTitle(title);
            if (isbns.size() == 1) {
                book = openLibraryService.queryIsbn(isbns.get(0));
            }
        } catch (BookNotFoundException e) {
            LOG.warn(String.format("OL search failed: %s", e.getMessage()));
        }
        if (book.getIsbn10() == null) {
            LOG.info(String.format("Querying Amazon for %s", title));
            book.merge(amazonService.searchKeyword(title));
        }
        if (book.getIsbn10() == null) {
            LOG.info(String.format("Querying Google for %s", title));
            try {
                book.merge(googleBooksService.queryTitle(title));
            } catch (BookNotFoundException e) {
                LOG.warn(String.format("Google search failed: %s", e.getMessage()));
            }
        }
        if (bookIsMissingInfo(book)) {
            book.merge(queryByIsbn(book));
        }

        try {
            LOG.info(objectMapper.writeValueAsString(book));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        LOG.info(String.format("GET took %d ms to process", endTime - startTime));

        ctx.json(book);
    };

    public Handler byIsbn = ctx -> {
        String isbnString = ctx.queryParam("isbn");
        if (!Isbn.validate(isbnString)) {
            ctx.status(400);
            ctx.result("ISBN was invalid");
        } else {
            Isbn isbn = Isbn.of(isbnString);
            LOG.info(String.format("Received query for isbn: %s", isbn.toString()));
            Book book = new Book.Builder()
                    .withIsbn10(Isbn10.convert(isbn))
                    .build();

            book.merge(queryByIsbn(book));

            ctx.json(book);
        }
    };

    private Book queryByIsbn(Book book) {
        // Query database here first?
        try {
            book.merge(openLibraryService.queryIsbn(book.getIsbn10()));
        } catch (BookNotFoundException e) {
            LOG.warn(String.format("Search failed: %s", e.getMessage()));
        }
        if (bookIsMissingInfo(book)) {
            try {
                book.merge(googleBooksService.queryIsbn(book.getIsbn10()));
            } catch (BookNotFoundException e) {
                LOG.warn(String.format("Search failed: %s", e.getMessage()));
            }
        }
        return book;
    }

    private boolean bookIsMissingInfo(Book book) {
        return book.getTitle() == null
                || book.getAuthor() == null
                || book.getDescription() == null
                || book.getIsbn10() == null
                || book.getPagecount() == null;
    }
}
