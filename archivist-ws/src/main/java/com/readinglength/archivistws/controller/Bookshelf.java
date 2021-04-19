package com.readinglength.archivistws.controller;

import com.readinglength.archivistws.dao.BookshelfDao;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn13;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.sql.SQLException;

@Controller
public class Bookshelf {
    private final BookshelfDao bookshelfDao;

    @Inject
    Bookshelf(BookshelfDao dao) {
        this.bookshelfDao = dao;
    }


    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hello from the bookshelf!";
    }

    @Post("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<String> insert(@Body Book book) {
        try {
            bookshelfDao.insertBook(book);
        } catch (SQLException e) {
            return HttpResponse.serverError(e.getMessage());
        }
        return HttpResponse.ok();
    }

    @Get("/byIsbn")
    public Mono<Book> queryBook(String isbnString) {
        if (!Isbn.validate(isbnString)) return Mono.error(new Throwable("ISBN was invalid."));
        try {
            return Mono.justOrEmpty(bookshelfDao.queryBook(Isbn.of(isbnString)));
        } catch (SQLException e) {
            return Mono.error(e);
        }
    }

    @Get("/byTitle")
    public Mono<Isbn13> queryIsbn(String titleString) {
        if (titleString.isEmpty()) return Mono.error(new Throwable("Title was blank."));
        try {
            return Mono.justOrEmpty(bookshelfDao.queryIsbn(titleString));
        } catch (SQLException e) {
            return Mono.error(e);
        }
    }

    @Get("/isbn")
    public Mono<Isbn13> getIsbn(String isbnString) {
        if (!Isbn.validate(isbnString)) return Mono.error(new Throwable("ISBN was invalid."));
        try {
            return Mono.justOrEmpty(bookshelfDao.queryForIsbn(Isbn.of(isbnString)));
        } catch (SQLException e) {
            return Mono.error(e);
        }
    }
}
