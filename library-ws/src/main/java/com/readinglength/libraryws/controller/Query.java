package com.readinglength.libraryws.controller;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
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
            LOG.info("Received query for: " + title);
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            // Check if title exists in our database index for isbn:title
            Isbn isbnFromTitle = archivistDao.getIsbnFromTitle(encodedTitle);
            if (isbnFromTitle != null) {
                // Query database for book from ISBN
                Book bookFromDatabase = archivistDao.getBookFromIsbn(isbnFromTitle);
                if (bookFromDatabase != null) {
                    ctx.status(200);
                    ctx.json(bookFromDatabase);
                    LOG.info("Book found in database: " + bookFromDatabase.getTitle());
                    return;
                } else {
                    // For some reason the ISBN and title exist in the database, but the book entry isn't there
                    LOG.error("Book not found in database for ISBN " + isbnFromTitle);
                    ctx.status(404);
                    ctx.result("Book not found for ISBN " + isbnFromTitle);
                    return;
                }
            } else {
                // Query researcher for book from title
                LOG.info("Looking externally for: " + title);
                Book bookFromExternal = researcherDao.getBookFromTitle(encodedTitle);
                if (bookFromExternal != null) {
                    ctx.status(200);
                    ctx.json(bookFromExternal);
                    LOG.info("Book found externally: " + bookFromExternal.getTitle());
                    boolean isbnInDatabase = archivistDao.isbnExistsInDatabase(bookFromExternal.getIsbn13());
                    if (!isbnInDatabase) {
                        LOG.info(bookFromExternal.getTitle() + " is a new book!");
                        // Give book to archivist
                    }

                    return;
                }
            }
            ctx.status(404);
            ctx.result("Book was not found in database for title " + title);
        }
    };
}
