package com.readinglength.libraryws.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.libraryws.auth.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ResearcherDao {
    private static final Logger LOG = LoggerFactory.getLogger(ResearcherDao.class);
    private final Authentication auth;
    private final HttpClient httpClient;
    private final String url;

    @Inject
    public ResearcherDao(Authentication auth) {
        this.auth = auth;
        url = System.getenv("RESEARCHER_WS");
        if (url == null) {
            String msg = "No configuration for researcher-dao. Add RESEARCHER_WS environment variable.";
            LOG.error(msg);
            throw new IllegalStateException(msg);
        }
        this.httpClient = HttpClient.newHttpClient();
    }

    public Book getBookFromTitle(String title) {
        HttpResponse<String> response = executeAuthenticatedGetRequest(url + "/byTitle?title=" + title);
        if (response != null && response.statusCode() == 200) {
            try {
                return new ObjectMapper().readValue(response.body(), Book.class);
            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        return null;
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
