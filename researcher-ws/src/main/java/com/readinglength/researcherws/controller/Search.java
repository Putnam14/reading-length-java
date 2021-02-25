package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.amazon.AmazonService;
import com.readinglength.researcherws.dao.google.GoogleBooksService;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import com.readinglength.researcherws.lib.BookNotFoundException;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.List;

@Controller("/search")
class Search {
    private static Logger LOG = LoggerFactory.getLogger(Search.class);

    private final OpenLibraryService openLibraryService;
    private final AmazonService amazonService;
    private final GoogleBooksService googleBooksService;

    @Inject
    Search(OpenLibraryService openLibraryService, AmazonService amazonService, GoogleBooksService googleBooksService) {
        this.openLibraryService = openLibraryService;
        this.amazonService = amazonService;
        this.googleBooksService = googleBooksService;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hi";
    }


    @Get("/byTitle")
    public Mono<Isbn> byTitle(@QueryValue String title) {
        Book book = new Book();
        try {
            LOG.info(String.format("Querying OL for %s", title));
            List<Isbn> isbns = openLibraryService.queryTitle(title);
            if (isbns.size() == 1) {
                openLibraryService.queryIsbn(isbns.get(0), book);
            }
        } catch (BookNotFoundException e) {
            LOG.info(String.format("OL search failed: %s", e.getMessage()));
        }
        if (book.getIsbn10() == null) {
            LOG.info(String.format("Querying Amazon for %s", title));
            amazonService.queryTitle(title, book);
        }
        if (book.getIsbn10() == null) {
            LOG.info(String.format("Querying Google for %s", title));
            try {
                googleBooksService.queryTitle(title, book);
            } catch (BookNotFoundException e) {
                LOG.info(String.format("Google search failed: %s", e.getMessage()));
            }
        }
        if (book.isMissingInfo()) {
            try {
                openLibraryService.queryIsbn(book.getIsbn10(), book);
                if (book.isMissingInfo()) {
                    googleBooksService.queryIsbn(book.getIsbn10(), book);
                }
            } catch (BookNotFoundException e) {
                LOG.info(String.format("Search failed: %s", e.getMessage()));
            }
        }

        try {
            LOG.info(new ObjectMapper().writeValueAsString(book));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Mono.justOrEmpty(book.getIsbn10());
    }


    @Get("/byIsbn")
    public Mono<Isbn> byIsbn(Isbn isbn) {
        // DataStoreResult = dataStoreDao.query(isbnQuery, indexTable);
        // if (DataStoreResult.getResultSet() > 0) return DataStoreResult.getIsbn();
        // Isbn isbn = Isbn.of(isbnQuery);
        // Book book = openLibraryDao.queryIsbn(isbn);
        // dataStoreDao.put(Book);
        // return ResponseEntity.ok(Book.getIsbn());
        LOG.info(String.format("Received query for isbn: %s", isbn.getIsbn()));
        Book book = new Book();

        try {
            // IF NOT IN DATABASE
            openLibraryService.queryIsbn(isbn, book);
            // If no page count, then ?
        }catch (BookNotFoundException e) {
            LOG.info(String.format("%s not found on OpenLibrary: %s", isbn.getIsbn(), e.getMessage()));
        }

        return Mono.justOrEmpty(book.getIsbn10());
    }
}
