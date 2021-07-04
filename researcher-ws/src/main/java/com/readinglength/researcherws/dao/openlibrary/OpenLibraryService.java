package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.lib.Isbn13;
import com.readinglength.researcherws.lib.BookNotFoundException;
import io.javalin.plugin.json.JavalinJackson;

import javax.inject.Inject;
import java.util.List;

public class OpenLibraryService {
    private final OpenLibraryDao openLibraryDao;
    private final ObjectMapper objectMapper;

    @Inject
    public OpenLibraryService(OpenLibraryDao openLibraryDao) {
        this.openLibraryDao = openLibraryDao;
        this.objectMapper = JavalinJackson.getObjectMapper();
    }

    public List<Isbn> queryTitle(String title) throws BookNotFoundException {
        OpenLibraryWork work;

        String openLibraryResponse = openLibraryDao.queryTitle(title);

        if (openLibraryResponse == null || "{}".equals(openLibraryResponse))
            throw new BookNotFoundException(title, "OpenLibrary");

        try {
            OpenLibraryTitleResponse response = objectMapper.readValue(openLibraryResponse, OpenLibraryTitleResponse.class);
            if (response.getDocs().size() == 0)
                throw new BookNotFoundException(title, "OpenLibrary");
            work = response.getDocs().get(0);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "OpenLibrary (JSON Error)");
        }

        return work.getIsbn();
    }

    public Book queryIsbn(Isbn isbn) throws BookNotFoundException {
        OpenLibraryEdition edition;

        String openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse == null || "{}".equals(openLibraryResponse))
            throw new BookNotFoundException(isbn.toString(), "OpenLibrary");

        try {
            edition = objectMapper.readValue(openLibraryResponse, OpenLibraryEdition.class);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(isbn.toString(), "OpenLibrary (JSON Error, Edition)");
        }

        return new Book.Builder()
                .withIsbn10(Isbn10.convert(isbn))
                .withCoverImage("https://covers.openlibrary.org/b/isbn/" + Isbn13.convert(isbn).toString() + "-L.jpg")
                .withTitle(edition.getTitle())
                .withPagecount(edition.getNumber_of_pages())
                .withDescription(edition.getDescription())
                .withPublishDate(edition.getPublish_date())
                .withPublisher(edition.getPublishers().get(0))
                .build();
    }
}
