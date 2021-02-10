package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryEdition;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.readinglength.lib.ws.RestClient.JSON_HEADERS;

@RestController
class Search {
    private static Logger LOG = LoggerFactory.getLogger(Search.class);

    private OpenLibraryService openLibraryService;

    @Autowired
    Search(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/search/byTitle")
    @ResponseBody
    public ResponseEntity<Isbn> byTitle(@RequestParam(name="title") String title) {
        Isbn isbn = null;

        try {
            List<Isbn> isbns = openLibraryService.queryTitle(title);
            if (isbns.size() == 1) {
                isbn = isbns.get(0);
            } else {
                //QUERY AMAZON OR GOOGLE HERE
                isbn = isbns.get(0);
            }
        } catch (BookNotFoundException e) {
            LOG.info(String.format("%s found in OpenLibrary: %s", title, e.getMessage()));
        }


        return byIsbn(isbn);
    }


    @GetMapping("/search/byIsbn")
    @ResponseBody
    public ResponseEntity<Isbn> byIsbn(@RequestParam(name="isbn") Isbn isbnQuery) {
        // DataStoreResult = dataStoreDao.query(isbnQuery, indexTable);
        // if (DataStoreResult.getResultSet() > 0) return DataStoreResult.getIsbn();
        // Isbn isbn = Isbn.of(isbnQuery);
        // Book book = openLibraryDao.queryIsbn(isbn);
        // dataStoreDao.put(Book);
        // return ResponseEntity.ok(Book.getIsbn());
        LOG.info(String.format("Received query for isbn: %s", isbnQuery));
        Isbn isbn = null;

        try {
            // IF NOT IN DATABASE
            isbn = openLibraryService.queryIsbn(isbnQuery);
            // If no page count, then ?
        }catch (BookNotFoundException e) {
            LOG.info(String.format("%s not found on OpenLibrary: %s", isbnQuery.getIsbn(), e.getMessage()));
        }

        return new ResponseEntity<>(isbn, JSON_HEADERS, HttpStatus.OK);
    }
}
