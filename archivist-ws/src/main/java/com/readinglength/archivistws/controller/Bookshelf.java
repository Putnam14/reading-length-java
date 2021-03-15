package com.readinglength.archivistws.controller;

import com.readinglength.archivistws.dao.BookshelfDao;
import com.readinglength.lib.Book;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

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
}
