package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class OpenLibraryService {
    private OpenLibraryDao openLibraryDao;
    private ObjectMapper objectMapper;

    @Autowired
    public OpenLibraryService(OpenLibraryDao openLibraryDao, ObjectMapper objectMapper) {
        this.openLibraryDao = openLibraryDao;
        this.objectMapper = objectMapper;
    }

    public List<Isbn> queryTitle(String title) throws BookNotFoundException {
        OpenLibraryWork work;

        ResponseEntity<String> openLibraryResponse = openLibraryDao.queryTitle(title);

        if (openLibraryResponse.getStatusCode() != HttpStatus.ACCEPTED) throw new BookNotFoundException(title, "OpenLibrary"); // Move this into OpenLibraryDao
        String jsonBody = openLibraryResponse.getBody();
        if (!StringUtils.hasLength(jsonBody) || "{}".equals(jsonBody)) throw new BookNotFoundException(title, "OpenLibrary");

        try {
            OpenLibraryTitleResponse response = objectMapper.readValue(jsonBody, OpenLibraryTitleResponse.class);
            work = response.getDocs().get(0);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "OpenLibrary_JSON");
        }

        return work.getIsbn();

    }

    public Isbn queryIsbn(Isbn isbn) throws BookNotFoundException {
        OpenLibraryEdition edition;

        ResponseEntity<String> openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse.getStatusCode() != HttpStatus.ACCEPTED) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");
        String jsonBody = openLibraryResponse.getBody();
        if (!StringUtils.hasLength(jsonBody) || "{}".equals(jsonBody)) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");

        try {
            edition = objectMapper.readValue(jsonBody, OpenLibraryEdition.class);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary_Edition_JSON");
        }

        return edition.getIsbn_10().get(0);

    }
}
