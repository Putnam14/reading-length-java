package com.readinglength.archivistws.controller;

import com.readinglength.lib.Book;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

@Controller
public class Bookshelf {
    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hello from the bookshelf!";
    }

    @Post("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Book> insert(@Body Book book) {
        return HttpResponse.ok(book);
    }
}
