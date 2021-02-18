package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.lib.BookNotFoundException;

import javax.inject.Inject;
import java.util.List;

public class OpenLibraryService {
    private final OpenLibraryDao openLibraryDao;
    private final ObjectMapper objectMapper;

    @Inject
    public OpenLibraryService(OpenLibraryDao openLibraryDao, ObjectMapper objectMapper) {
        this.openLibraryDao = openLibraryDao;
        this.objectMapper = objectMapper;
    }

    public List<Isbn> queryTitle(String title) throws BookNotFoundException {
        OpenLibraryWork work;

        String openLibraryResponse = openLibraryDao.queryTitle(title);

        if (openLibraryResponse == null) throw new BookNotFoundException(title, "OpenLibrary"); // Move this into OpenLibraryDao
        if ("{}".equals(openLibraryResponse)) throw new BookNotFoundException(title, "OpenLibrary");

        try {
            OpenLibraryTitleResponse response = objectMapper.readValue(openLibraryResponse, OpenLibraryTitleResponse.class);
            work = response.getDocs().get(0);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "OpenLibrary_JSON");
        }

        return work.getIsbn();

    }

    public Isbn queryIsbn(Isbn isbn) throws BookNotFoundException {
        OpenLibraryEdition edition;

        String openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse == null) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");
        if ("{}".equals(openLibraryResponse)) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");

        try {
            edition = objectMapper.readValue(openLibraryResponse, OpenLibraryEdition.class);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary_Edition_JSON");
        }

        Isbn result = null;
        if (edition.getIsbn_10() != null && edition.getIsbn_10().size() > 0) {
            result = edition.getIsbn_10().get(0);
        } else if (edition.getIsbn_13() != null && edition.getIsbn_13().size() > 0) {
            result = Isbn10.convert(edition.getIsbn_13().get(0));
        }
        return result;

    }
}
