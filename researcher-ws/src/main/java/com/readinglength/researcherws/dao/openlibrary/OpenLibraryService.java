package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class OpenLibraryService {
    private static Logger LOG = LoggerFactory.getLogger(OpenLibraryService.class);
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

        if (openLibraryResponse == null) throw new BookNotFoundException(title, "OpenLibrary");
        if ("{}".equals(openLibraryResponse)) throw new BookNotFoundException(title, "OpenLibrary");

        try {
            OpenLibraryTitleResponse response = objectMapper.readValue(openLibraryResponse, OpenLibraryTitleResponse.class);
            if (response.getDocs().size() == 0) throw new BookNotFoundException(title, "OpenLibrary_NotFound");
            work = response.getDocs().get(0);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "OpenLibrary_JSON");
        }

        return work.getIsbn();

    }

    public Book queryIsbn(Isbn isbn, Book book) throws BookNotFoundException {
        OpenLibraryEdition edition;

        String openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse == null) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");
        if ("{}".equals(openLibraryResponse)) throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary");

        try {
            edition = objectMapper.readValue(openLibraryResponse, OpenLibraryEdition.class);
            LOG.info(objectMapper.writeValueAsString(edition));
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(isbn.getIsbn(), "OpenLibrary_Edition_JSON");
        }

        if (edition.getIsbn_10() != null && edition.getIsbn_10().size() > 0) {
            if (book.getIsbn10() == null) book.setIsbn10(edition.getIsbn_10().get(0));
        } else if (edition.getIsbn_13() != null && edition.getIsbn_13().size() > 0) {
            if (book.getIsbn13() == null) book.setIsbn13(edition.getIsbn_13().get(0));
        }
        if (edition.getTitle() != null) {
            LOG.info("Setting title");
            if (book.getTitle() == null) book.setTitle(edition.getTitle());
        }
        if (edition.getNumber_of_pages() != null) {
            if (book.getPagecount() == null) book.setPagecount(edition.getNumber_of_pages());
        }
        if (edition.getDescription() != null) {
            if (book.getDescription() == null) book.setDescription(edition.getDescription());
        }
        return book;

    }
}
