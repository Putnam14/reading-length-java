package com.readinglength.researcherws.controller;

import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.amazon.AmazonService;
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

    @Inject
    Search(OpenLibraryService openLibraryService, AmazonService amazonService) {
        this.openLibraryService = openLibraryService;
        this.amazonService = amazonService;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hi";
    }


    @Get("/byTitle")
    public Mono<Isbn> byTitle(@QueryValue String title) {
        Isbn isbn = null;

        try {
            List<Isbn> isbns = openLibraryService.queryTitle(title);
            if (isbns.size() == 1) {
                isbn = isbns.get(0);
            } else {
                //QUERY AMAZON OR GOOGLE HERE
                isbn = amazonService.queryTitle(title);
            }
        } catch (BookNotFoundException e) {
            LOG.info(String.format("%s not found in OpenLibrary: %s", title, e.getMessage()));
        }


        return byIsbn(isbn);
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
        Isbn isbnRes = null;

        try {
            // IF NOT IN DATABASE
            isbnRes = openLibraryService.queryIsbn(isbn);
            // If no page count, then ?
        }catch (BookNotFoundException e) {
            LOG.info(String.format("%s not found on OpenLibrary: %s", isbn.getIsbn(), e.getMessage()));
        }

        return Mono.justOrEmpty(isbnRes);
    }
}
