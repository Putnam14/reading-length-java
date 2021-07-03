package com.readinglength.archivistws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.archivistws.controller.Bookshelf;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class ArchivistWsApplication {
    public static void main(String[] args) {
        ArchivistComponent archivistComponent = DaggerArchivistComponent.create();
        Bookshelf bookshelf = archivistComponent.bookshelf();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JavalinJackson.configure(objectMapper);

        Javalin app = Javalin.create().start(getPort());

        app.get("/", bookshelf.index);
        app.get("/books/isbn", bookshelf.queryBookByIsbn);
        app.post("/books/insert", bookshelf.insertBook);
        app.get("/isbns/title", bookshelf.queryIsbnByTitle);
        app.get("/isbns", bookshelf.isbnExists);
        app.get("/wordcounts", bookshelf.wordcountByIsbn);
        app.put("/wordcounts", bookshelf.insertWordcount);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            return 8080;
        }
        return Integer.valueOf(port);
    }
}