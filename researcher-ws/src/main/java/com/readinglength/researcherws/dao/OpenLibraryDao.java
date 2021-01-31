package com.readinglength.researcherws.dao;

import com.readinglength.lib.ws.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpenLibraryDao {
    private RestClient restClient;
    private static HttpHeaders headers = new HttpHeaders();

    static {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Autowired
    OpenLibraryDao(RestClient restClient) {
        restClient.setServerBaseUri("http://openlibrary.org/search.json");
        this.restClient = restClient;
    }

    public ResponseEntity<String> queryTitle(String title) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("title", title.replace(' ','+'));
        queryParams.put("limit","1");
        return new ResponseEntity<>(restClient.get(queryParams), headers, HttpStatus.ACCEPTED);
    }
}
