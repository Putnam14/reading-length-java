package com.readinglength.archivistws;

import com.readinglength.archivistws.controller.Bookshelf;
import io.javalin.Javalin;

public class ArchivistWsApplication {
    public static void main(String[] args) {
        ArchivistComponent archivistComponent = DaggerArchivistComponent.create();
        Bookshelf bookshelf = archivistComponent.bookshelf();
        Javalin app = Javalin.create().start(8080);
        app.get("/", bookshelf.index);
        app.get("/books/isbn", bookshelf.queryBookByIsbn);
        app.post("/books/insert", bookshelf.insertBook);
        app.get("/isbns/title", bookshelf.queryIsbnByTitle);
        app.get("/isbns", bookshelf.isbnExists);
    }
}