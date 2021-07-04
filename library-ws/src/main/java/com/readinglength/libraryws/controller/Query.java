package com.readinglength.libraryws.controller;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Wordcount;
import com.readinglength.libraryws.dao.ArchivistDao;
import com.readinglength.libraryws.dao.ResearcherDao;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Query {
    private static final Logger LOG = LoggerFactory.getLogger(Query.class);
    private ArchivistDao archivistDao;
    private ResearcherDao researcherDao;

    @Inject
    public Query(ArchivistDao archivistDao, ResearcherDao researcherDao) {
        this.archivistDao = archivistDao;
        this.researcherDao = researcherDao;
    }

    public Handler queryBookByTitle = ctx -> {
        String title = ctx.queryParam("title");
        if (title == null || title.isEmpty()) {
            ctx.status(400);
        } else {
            Book book = findBook(title);
            if (book != null) {
                Wordcount wordcount = findWordcount(book);
                book = book.toBuilder()
                        .withWordcount(wordcount)
                        .build();
                ctx.status(200);
                ctx.json(book);
            } else {
                ctx.status(404);
                ctx.result("Book was not found in database for title " + title);
            }
        }
    };

    public Handler queryBookByIsbn = ctx -> {
        String isbnString = ctx.queryParam("isbn");
        if (isbnString != null && Isbn.validate(isbnString)) {
            Book book = findBookByIsbnString(isbnString);
            if (book != null) {
                ctx.status(200);
                ctx.json(book);
            } else {
                ctx.status(404);
                ctx.result("Book was not found in database for isbn " + isbnString);
            }
        } else {
            ctx.status(400);
            ctx.result("Invalid isbn: " + isbnString);
        }
    };

    private Book findBookByIsbnString(String isbnString) {
        if (isbnString != null && Isbn.validate(isbnString)) {
            Isbn isbn = Isbn.of(isbnString);
            Book book = findBookByIsbn(isbn);
            if (book != null) {
                Wordcount wordcount = findWordcount(book);
                book = book.toBuilder()
                        .withWordcount(wordcount)
                        .build();
                return book;
            }
        }
        return null;
    }

    private Book findBook(String title) {
        LOG.info("Received query for: " + title);
        if (Isbn.validate(title)) {
            LOG.info(title + " is an ISBN.");
            return findBookByIsbnString(title);
        }
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        // Check if title exists in our database index for isbn:title
        Isbn isbnFromTitle = archivistDao.getIsbnFromTitle(encodedTitle);
        if (isbnFromTitle != null) {
            // Query database for book from ISBN
            return findBookByIsbn(isbnFromTitle);
        } else {
            // Query researcher for book from title
            LOG.info("Looking externally for: " + title);
            Book bookFromExternal = researcherDao.getBookFromTitle(encodedTitle);
            if (bookFromExternal != null) {
                LOG.info("Book found externally: " + bookFromExternal.getTitle());
                archivistDao.handleExternalBook(bookFromExternal);
                return bookFromExternal;
            }
        }
        return null;
    }

    private Book findBookByIsbn(Isbn isbn) {
        Book bookFromDatabase = archivistDao.getBookFromIsbn(isbn);
        if (bookFromDatabase != null) {
            LOG.info("Book found in database: " + bookFromDatabase.getTitle());
            return bookFromDatabase;
        }
        Book bookFromExternal = researcherDao.getBookFromIsbn(isbn);
        if (bookFromExternal != null) {
            LOG.info("Book found externally: " + bookFromExternal.getTitle());
            return bookFromExternal;
        }
        LOG.error("Book not found in database for ISBN " + isbn);
        return null;
    }

    private Wordcount findWordcount(Book book) {
        LOG.info("Querying wordcount for ISBN: " + book.getIsbn13());
        Wordcount wordcount = archivistDao.getWordcountFromIsbn(book.getIsbn13());
        if (wordcount == null) {
            wordcount = new Wordcount.Builder()
                    .withIsbn(book.getIsbn13())
                    .withWords(book.getPagecount() * 350)
                    .withUserId(-1)
                    .withType(Wordcount.WordcountType.GUESS.getId())
                    .build();
        }
        return wordcount;
    }
}
