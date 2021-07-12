package com.readinglength.researcherws.dao.openlibrary;

import com.readinglength.lib.Isbn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenLibraryDao {
    private static final Logger LOG = LoggerFactory.getLogger(OpenLibraryDao.class);
    private static final String BASE_URL = "https://openlibrary.org/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Inject
    public OpenLibraryDao() {}

    String queryTitle(String title) {
        String requestUrl = String.format("%ssearch.json?title=%s&limit=1", BASE_URL, title);

        HttpResponse<String> response = executeGetRequest(requestUrl);
        if (response != null && response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    String queryIsbn(Isbn isbn) {
        String requestUrl = String.format("%sisbn/%s.json", BASE_URL, isbn.toString());

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
