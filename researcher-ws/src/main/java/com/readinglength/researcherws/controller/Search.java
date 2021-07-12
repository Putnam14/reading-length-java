package com.readinglength.researcherws.controller;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.dao.amazon.AmazonService;
import com.readinglength.researcherws.dao.google.GoogleBooksService;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import com.readinglength.researcherws.lib.BookNotFoundException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public
class Search {
    private static Logger LOG = LoggerFactory.getLogger(Search.class);

    private OpenLibraryService openLibraryService;
    private AmazonService amazonService;
    private GoogleBooksService googleBooksService;

    @Inject
    public Search(OpenLibraryService openLibraryService, AmazonService amazonService, GoogleBooksService googleBooksService) {
        this.openLibraryService = openLibraryService;
        this.amazonService = amazonService;
        this.googleBooksService = googleBooksService;
    }

    public void index(Context ctx) {
        ctx.result("Hi");
    }

    public void byTitle(Context ctx) {
        long startTime = System.currentTimeMillis();
        String title = ctx.queryParam("title");
        Book book = new Book.Builder().build();
        if (title != null) {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            try {
                List<Isbn> isbns = openLibraryService.queryTitle(encodedTitle);
                if (isbns.size() == 1 || isbns.size() == 2) {
                    book = openLibraryService.queryIsbn(isbns.get(0));
                }
            } catch (BookNotFoundException e) {
                LOG.warn(String.format("OL search for '%s' failed: %s", title, e.getMessage()));
            }
            if (book.getIsbn10() == null) {
                book.merge(amazonService.searchKeyword(encodedTitle));
            }
            if (book.getIsbn10() == null) {
                try {
                    book.merge(googleBooksService.queryTitle(encodedTitle));
                } catch (BookNotFoundException e) {
                    LOG.warn(String.format("Google search for '%s' failed: %s", title, e.getMessage()));
                }
            }
            if (bookIsMissingInfo(book)) {
                book = queryByIsbn(book);
            }
        }
        ctx.json(book);
        long endTime = System.currentTimeMillis();
        LOG.info(String.format("Search for '%s' ('%s') complete in %dms.", title, book.getTitle(), endTime - startTime));
    }

    public void byIsbn(Context ctx) {
        long startTime = System.currentTimeMillis();
        String isbnString = ctx.queryParam("isbn");
        if (!Isbn.validate(isbnString)) {
            ctx.status(400);
            ctx.result(String.format("ISBN '%s' was invalid", isbnString));
        } else {
            Isbn isbn = Isbn.of(isbnString);
            Book book = queryByIsbn(new Book.Builder().withIsbn10(Isbn10.convert(isbn)).build());

            ctx.json(book);
            long endTime = System.currentTimeMillis();
            LOG.info(String.format("Search for '%s' ('%s') complete in %dms.", isbnString, book.getTitle(), endTime - startTime));
        }
    }

    private Book queryByIsbn(Book book) {
        Book result = new Book.Builder().build();
        try {
            result.merge(openLibraryService.queryIsbn(book.getIsbn10()));
        } catch (BookNotFoundException e) {
            LOG.warn(String.format("OL search for '%s' failed: %s", book.getIsbn10(), e.getMessage()));
        }
        if (bookIsMissingInfo(book)) {
            try {
                result.merge(googleBooksService.queryIsbn(book.getIsbn10()));
            } catch (BookNotFoundException e) {
                LOG.warn(String.format("Google search for '%s' failed: %s", book.getIsbn10(), e.getMessage()));
            }
        }
        result.merge(book);
        return result;
    }

    private boolean bookIsMissingInfo(Book book) {
        return book.getTitle() == null
                || book.getAuthor() == null
                || book.getDescription() == null
                || book.getIsbn10() == null
                || book.getPagecount() == null;
    }
}
