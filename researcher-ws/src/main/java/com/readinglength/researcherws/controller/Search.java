package com.readinglength.researcherws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryDao;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryEdition;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryTitleResponse;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.readinglength.lib.ws.RestClient.JSON_HEADERS;

@RestController
public class Search {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static Logger LOG = LoggerFactory.getLogger(Search.class);

    private OpenLibraryDao openLibraryDao;

    @Autowired
    public Search(OpenLibraryDao openLibraryDao) {
        this.openLibraryDao = openLibraryDao;
    }

    @GetMapping("/search/byTitle")
    @ResponseBody
    public ResponseEntity<Isbn> byTitle(@RequestParam(name="title") String title) throws JsonProcessingException {
        // DataStoreResult = dataStoreDao.query(title, indexTable);
        // if (DataStoreResult.getResultSet() > 0) return DataStoreResult.getIsbn();
        // Work work = openLibraryDao.queryTitle(title);
        // dataStoreDao.put(work);
        // if (work.editionCount() == 1) return byIsbn(work.getIsbn());
        // if (work.editionCount() > 1) return byIsbn(amazonDao.byTitle(title));
        // return ResponseEntity.notFound().build();
        OpenLibraryWork work;
        Isbn isbn = null;

        try {
            work = queryOpenLibraryForTitle(title);
            if (work == null) return ResponseEntity.notFound().build(); // Query Amazon

            List<String> isbns = work.getIsbn();

            if (isbns.size() == 1) {
                isbn = Isbn.of(isbns.get(0));
            } else {
                //QUERY AMAZON HERE
                isbn = Isbn.of(isbns.get(0));
            }
        } catch (BookNotFoundException e) {
            LOG.info(String.format("Not found in OpenLibrary: %s", title));
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
            OpenLibraryEdition edition = queryOpenLibraryForEdition(isbnQuery);
            isbn = Isbn.of(edition.getIsbn_10().get(0));
        }catch (BookNotFoundException e) {
            LOG.info(String.format("Not found in OpenLibrary: %s", isbnQuery.getIsbn()));
        }

        return new ResponseEntity<>(isbn, JSON_HEADERS, HttpStatus.OK);
    }

    private OpenLibraryWork queryOpenLibraryForTitle(String title) throws BookNotFoundException {
        OpenLibraryWork work;

        ResponseEntity<String> openLibraryResponse = openLibraryDao.queryTitle(title);

        if (openLibraryResponse.getStatusCode() != HttpStatus.ACCEPTED) throw new BookNotFoundException(title, "OpenLibrary"); // Move this into OpenLibraryDao
        String jsonBody = openLibraryResponse.getBody();
        if (!StringUtils.hasLength(jsonBody) || "{}".equals(jsonBody)) throw new BookNotFoundException(title, "OpenLibrary");

        try {
            OpenLibraryTitleResponse response = OBJECT_MAPPER.readValue(jsonBody, OpenLibraryTitleResponse.class);
            work = response.getDocs().get(0);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "OpenLibrary_JSON");
        }

        return work;

    }

    private OpenLibraryEdition queryOpenLibraryForEdition(Isbn isbn) throws BookNotFoundException {
        OpenLibraryEdition edition;

        ResponseEntity<String> openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse.getStatusCode() != HttpStatus.ACCEPTED) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");
        String jsonBody = openLibraryResponse.getBody();
        if (!StringUtils.hasLength(jsonBody) || "{}".equals(jsonBody)) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");

        try {
            edition = OBJECT_MAPPER.readValue(jsonBody, OpenLibraryEdition.class);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary_Edition_JSON");
        }

        return edition;

    }


    private class BookNotFoundException extends Exception {
        private String message;

        BookNotFoundException(String title, String service) {
            this.message = String.format("'%s' not found on %s.", title, service);
        }

        public String getMessage() {
            return message;
        }
    }
}
