package com.readinglength.researcherws.dao.google;

import com.readinglength.lib.Isbn;
import com.readinglength.lib.ws.RestClient;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class GoogleBooksDao {
    private final String apiKey;
    private final RestClient restClient;

    public GoogleBooksDao(RestClient restClient, URL baseUrl, String key) {
        restClient.setClient(baseUrl);
        this.restClient = restClient;
        this.apiKey = key;

    }

    public String queryTitle(String title) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("q", title);
        queryParams.put("printType", "BOOKS");
        queryParams.put("key", apiKey);
        synchronized (restClient) {
            return restClient.get("volumes", queryParams);
        }
    }

    public String queryIsbn(Isbn isbn) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("q", String.format("isbn:%s", isbn.toString()));
        queryParams.put("key", apiKey);
        synchronized (restClient) {
            return restClient.get("volumes", queryParams);
        }
    }

}
