package com.readinglength.archivistws.controller;

import com.readinglength.archivistws.dao.BookshelfDao;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn13;
import io.javalin.http.Handler;

import javax.inject.Inject;
import java.sql.SQLException;

public class Bookshelf {
    private BookshelfDao bookshelfDao;

    public Handler index = ctx -> ctx.result("Hello from the bookshelf!");

    public Handler insertBook = ctx -> {
        Book book = ctx.bodyAsClass(Book.class);
        try {
            bookshelfDao.insertBook(book);
        } catch (SQLException e) {
            ctx.status(500);
            ctx.result("Something went wrong on our end with the database.");
        }
        ctx.status(201);
    };

    public Handler queryBookByIsbn = ctx -> {
        String isbnString = ctx.queryParam("isbn");
        if (!Isbn.validate(isbnString)) {
            ctx.status(400);
            ctx.result("Invalid ISBN");
        } else {
            Book book = bookshelfDao.queryBook(Isbn.of(isbnString));
            if (book != null) {
                ctx.json(book);
            } else {
                ctx.status(404);
                ctx.result("Book was not found for ISBN " + isbnString);
            }
        }
    };

    public Handler queryIsbnByTitle = ctx -> {
        String title = ctx.queryParam("title");
        if (title == null || title.isEmpty()) {
            ctx.status(400);
        } else {
            Isbn13 isbn = bookshelfDao.queryIsbn(title);
            if (isbn != null) {
                ctx.json(isbn);
            } else {
                ctx.status(404);
                ctx.result("ISBN was not found in database for title " + title);
            }
        }
    };

    public Handler isbnExists = ctx -> {
        String isbnString = ctx.queryParam("isbn");
        if (!Isbn.validate(isbnString)) {
            ctx.status(400);
        } else {
            if (bookshelfDao.queryForIsbn(Isbn.of(isbnString))) {
                ctx.status(200);
                ctx.result(String.format("Entry for %s exists in database.", isbnString));
            } else {
                ctx.status(404);
                ctx.result(String.format("%s was not found in database.", isbnString));
            }
        }
    };

    @Inject
    public Bookshelf(BookshelfDao dao) {
        this.bookshelfDao = dao;
    }
}
