package com.readinglength.researcherws.dao.google;

import com.readinglength.lib.Isbn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GoogleBooksDao {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleBooksDao.class);
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final String apiKey;

    public GoogleBooksDao(String key) {
        this.apiKey = key;
    }

    String queryTitle(String title) {
        String requestUrl = String.format("%svolumes?q=%s&printType=BOOKS&key=%s", BASE_URL, title, apiKey);

        HttpResponse<String> response = executeGetRequest(requestUrl);
        if (response != null && response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    String queryIsbn(Isbn isbn) {
        String requestUrl = String.format("%svolumes?q=isbn:%s&key=%s", BASE_URL, isbn.toString(), apiKey);

        HttpResponse<String> response = executeGetRequest(requestUrl);
        if (response != null && response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    private HttpResponse<String> executeGetRequest(String requestUrl) {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        try {
            return HTTP_CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }
}
