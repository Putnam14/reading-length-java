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

@Controller
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
    public Mono<Book> byTitle(@QueryValue String title) {
        long startTime = System.currentTimeMillis();
        Book book = new Book();
        try {
            LOG.info(String.format("Querying OL for %s", title));
            List<Isbn> isbns = openLibraryService.queryTitle(title);
            if (isbns.size() == 1) {
                book.merge(openLibraryService.queryIsbn(isbns.get(0)));
            }
        } catch (BookNotFoundException e) {
            LOG.warn(String.format("OL search failed: %s", e.getMessage()));
        }
        if (book.getIsbn10() == null) {
            LOG.info(String.format("Querying Amazon for %s", title));
            book.merge(amazonService.queryTitle(title));
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
            LOG.info(new ObjectMapper().writeValueAsString(book));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        LOG.info(String.format("GET took %d ms to process", endTime - startTime));

        return Mono.justOrEmpty(book);
    }


    @Get("/byIsbn")
    public Mono<Book> byIsbn(String isbnString) {
        if (!Isbn.validate(isbnString)) return Mono.error(new Throwable("ISBN was invalid"));
        Isbn isbn = Isbn.of(isbnString);
        LOG.info(String.format("Received query for isbn: %s", isbn.getIsbn()));
        Book book = new Book();
        book.setIsbn10(Isbn10.convert(isbn));

        book.merge(queryByIsbn(book));

        return Mono.justOrEmpty(book);
    }

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
