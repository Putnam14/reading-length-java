package com.readinglength.researcherws.dao.openlibrary;

import com.readinglength.lib.Isbn;
import com.readinglength.lib.ws.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.readinglength.lib.ws.RestClient.JSON_HEADERS;

@Component
public class OpenLibraryDao {
    private RestClient restClient;

    @Autowired
    OpenLibraryDao(RestClient restClient) {
        restClient.setServerBaseUri("http://openlibrary.org/");
        this.restClient = restClient;
    }

    public ResponseEntity<String> queryTitle(String title) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("title", title);
        queryParams.put("limit","1");
        return new ResponseEntity<>(restClient.get("search.json", queryParams), JSON_HEADERS, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> queryIsbn(Isbn isbn) {
        String requestString = String.format("%s.json", isbn.getIsbn());
        return new ResponseEntity<>(restClient.get("isbn/", requestString ), JSON_HEADERS, HttpStatus.ACCEPTED);
    }
}
