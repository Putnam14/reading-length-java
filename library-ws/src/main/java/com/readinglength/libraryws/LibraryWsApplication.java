package com.readinglength.libraryws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.libraryws.controller.Query;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class LibraryWsApplication {
    public static void main(String[] args) {
        LibraryComponent libraryComponent = DaggerLibraryComponent.create();
        Query query = libraryComponent.query();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JavalinJackson.configure(objectMapper);

        Javalin app = Javalin.create().start(getPort());

        app.get("/byTitle", query.queryBookByTitle);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            return 8080;
        }
        return Integer.valueOf(port);
    }
}