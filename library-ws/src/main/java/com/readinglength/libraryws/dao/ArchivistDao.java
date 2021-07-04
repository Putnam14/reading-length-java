package com.readinglength.libraryws.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Wordcount;
import com.readinglength.libraryws.auth.Authentication;
import io.javalin.plugin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArchivistDao {
    private static final Logger LOG = LoggerFactory.getLogger(ArchivistDao.class);
    private final Authentication auth;
    private final HttpClient httpClient;
    private final String url;
    private final Executor executorService;

    @Inject
    public ArchivistDao(Authentication auth) {
        this.auth = auth;
        url = System.getenv("ARCHIVIST_WS");
        if (url == null) {
            String msg = "No configuration for archivist-dao. Add ARCHIVIST_WS environment variable.";
            LOG.error(msg);
            throw new IllegalStateException(msg);
        }
        this.httpClient = java.net.http.HttpClient.newHttpClient();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public Isbn getIsbnFromTitle(String title) {
        HttpResponse<String> response = executeAuthenticatedGetRequest(url + "/isbns/title?title=" + title);
        if (response != null && response.statusCode() == 200) {
            String entity = response.body();
            return Isbn.of(JsonPath.read(entity, "$.isbn"));
        }
        return null;
    }

    public Book getBookFromIsbn(Isbn isbn) {
        HttpResponse<String> response = executeAuthenticatedGetRequest(url + "/books/isbn?isbn=" + isbn);
        if (response != null && response.statusCode() == 200) {
            try {
                return JavalinJackson.getObjectMapper().readValue(response.body(), Book.class);
            } catch (IOException e) {
                // JSON processing exception
                LOG.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    public Wordcount getWordcountFromIsbn(Isbn isbn) {
        HttpResponse<String> response = executeAuthenticatedGetRequest(url + "/wordcounts?isbn=" + isbn);
        if (response != null && response.statusCode() == 200) {
            try {
                return JavalinJackson.getObjectMapper().readValue(response.body(), Wordcount.class);
            } catch (IOException e) {
                // JSON processing exception
                LOG.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    public void handleExternalBook(Book book) {
        Runnable externalBookTask = () -> {
            boolean isbnInDatabase = isbnExistsInDatabase(book.getIsbn13());
            if (!isbnInDatabase) {
                LOG.info(book.getTitle() + " is a new book!");
                insertBook(book);
            }
        };
        executorService.execute(externalBookTask);
    }

    private boolean isbnExistsInDatabase(Isbn isbn) {
        HttpResponse response = executeAuthenticatedGetRequest(url + "/isbns?isbn=" + isbn);
        return response != null && response.statusCode() == 200;
    }

    private void insertBook(Book book) {
        String requestUrl = url + "/books/insert";
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .setHeader("Authorization", "Bearer " + auth.getToken(requestUrl))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(JavalinJackson.getObjectMapper().writeValueAsString(book)))
                    .build();
            httpClient.send(postRequest, HttpResponse.BodyHandlers.discarding());
        } catch (JsonProcessingException e) {
            LOG.error("Issue writing book to JSON", e);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(e.getLocalizedMessage());
        }
    }

    private HttpResponse<String> executeAuthenticatedGetRequest(String requestUrl) {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .setHeader("Authorization", "Bearer " + auth.getToken(requestUrl))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        try {
            return httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }
}
