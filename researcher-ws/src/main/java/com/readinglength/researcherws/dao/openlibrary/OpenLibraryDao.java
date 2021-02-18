package com.readinglength.researcherws.dao.openlibrary;

import com.readinglength.lib.Isbn;
import com.readinglength.lib.ws.RestClient;

import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class OpenLibraryDao {
    private RestClient restClient;

    OpenLibraryDao(RestClient restClient) throws MalformedURLException {
        restClient.setClient(new URL("http://openlibrary.org/"));
        this.restClient = restClient;
    }

    public String queryTitle(String title) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("title", title);
        queryParams.put("limit","1");
        return restClient.get("search.json", queryParams);
    }

    public String queryIsbn(Isbn isbn) {
        String requestString = String.format("%s.json", isbn.getIsbn());
        return restClient.get("isbn/", requestString );
    }
}
