package com.readinglength.researcherws.dao.openlibrary;

import com.readinglength.lib.Isbn;
import com.readinglength.lib.ws.RestClient;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpenLibraryDao {
    private final RestClient restClient;

    @Inject
    public OpenLibraryDao(RestClient restClient) {
        try {
            restClient.setClient(new URL("https://openlibrary.org/"));
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        this.restClient = restClient;
    }

    public String queryTitle(String title) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("title", title);
        queryParams.put("limit","1");
        synchronized (restClient) {
            return restClient.get("search.json", queryParams);
        }
    }

    public String queryIsbn(Isbn isbn) {
        String requestString = String.format("%s.json", isbn.getIsbn());
        synchronized (restClient) {
            return restClient.get("isbn/", requestString);
        }
    }
}
