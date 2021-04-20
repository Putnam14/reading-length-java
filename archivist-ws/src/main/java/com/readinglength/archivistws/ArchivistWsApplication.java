package com.readinglength.archivistws;

import com.readinglength.archivistws.controller.Bookshelf;
import com.readinglength.archivistws.dao.BookshelfDao;
import com.readinglength.archivistws.lib.HikariDataSourceFactory;
import io.javalin.Javalin;

public class ArchivistWsApplication {
    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf(new BookshelfDao(new HikariDataSourceFactory()));
        Javalin app = Javalin.create().start(8080);
        app.get("/", bookshelf.index);
        app.get("/books/isbn", bookshelf.queryBookByIsbn);
        app.post("/books/insert", bookshelf.insertBook);
        app.get("/isbns/title", bookshelf.queryIsbnByTitle);
        app.get("/isbns", bookshelf.isbnExists);
    }
}