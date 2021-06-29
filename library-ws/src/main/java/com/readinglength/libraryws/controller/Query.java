package com.readinglength.libraryws.controller;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.libraryws.dao.ArchivistDao;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Query {
    private static final Logger LOG = LoggerFactory.getLogger(Query.class);
    private ArchivistDao archivistDao;

    @Inject
    public Query(ArchivistDao archivistDao) {
        this.archivistDao = archivistDao;
    }

    public Handler queryBookByTitle = ctx -> {
        String title = ctx.queryParam("title");
        if (title == null || title.isEmpty()) {
            ctx.status(400);
        } else {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            Isbn isbn = archivistDao.getIsbnFromTitle(encodedTitle);
            if (isbn != null) {
                Book response = archivistDao.getBookFromIsbn(isbn);
                if (response != null) {
                    ctx.json(response);
                } else {
                    LOG.error("Book not found in database for ISBN " + isbn);
                    ctx.status(404);
                    ctx.result("Book not found for ISBN " + isbn);
                }
            } else {
                ctx.status(404);
                ctx.result("ISBN was not found in database for title " + title);
            }
        }
    };
}
