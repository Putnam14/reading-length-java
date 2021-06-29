package com.readinglength.libraryws.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.libraryws.auth.Authentication;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ArchivistDao {
    private static Logger LOG = LoggerFactory.getLogger(ArchivistDao.class);
    private Authentication auth;
    private HttpClient httpClient;
    private String url;

    @Inject
    public ArchivistDao(Authentication auth) {
        this.auth = auth;
        url = System.getenv("ARCHIVIST_WS");
        if (url == null) {
            String msg = "No configuration for archivist-dao. Add ARCHIVIST_WS environment variable.";
            LOG.error(msg);
            throw new IllegalStateException(msg);
        }
        this.httpClient = HttpClientBuilder.create().build();
    }

    public Isbn getIsbnFromTitle(String title) {
        String requestUrl = url + "/isbns/title/?title=" + title;
        HttpGet getRequest = new HttpGet(requestUrl);
        getRequest.setHeader("Authorization", "Bearer " + auth.getToken(requestUrl));
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    LOG.info("Non-200 response: " + EntityUtils.toString(entity, StandardCharsets.UTF_8));
                    return null;
                }
                String res = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return Isbn.of(JsonPath.read(res, "$.isbn"));
            }
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Book getBookFromIsbn(Isbn isbn) {
        String requestUrl = url + "/books/isbn/?isbn=" + isbn;
        LOG.info(requestUrl);
        HttpGet getRequest = new HttpGet(requestUrl);
        getRequest.setHeader("Authorization", "Bearer " + auth.getToken(requestUrl));
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    LOG.info("Non-200 response: " + EntityUtils.toString(entity, StandardCharsets.UTF_8));
                    return null;
                }
                String res = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                LOG.info(res);
                return new ObjectMapper().readValue(res, Book.class);
            }
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }
}
