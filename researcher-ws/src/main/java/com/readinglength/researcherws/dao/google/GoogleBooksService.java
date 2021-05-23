package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.lib.BookNotFoundException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class GoogleBooksService {
    private final GoogleBooksDao googleBooksDao;
    private final ObjectMapper objectMapper;

    @Inject
    public GoogleBooksService(GoogleBooksDao googleBooksDao) {
        this.googleBooksDao = googleBooksDao;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public Book queryTitle(String title) throws BookNotFoundException {
        return processResponse(googleBooksDao.queryTitle(title), title);
    }

    public Book queryIsbn(Isbn isbn) throws BookNotFoundException {
        return processResponse(googleBooksDao.queryIsbn(isbn), isbn.toString());
    }

    private Book processResponse(String googleBooksResponse, String title) throws BookNotFoundException  {
        if (googleBooksResponse == null || "{}".equals(googleBooksResponse))
            throw new BookNotFoundException(title, "Google");

        try {
            GoogleBooksVolumesResult response = objectMapper.readValue(googleBooksResponse, GoogleBooksVolumesResult.class);

            if (response.getTotalItems() == 0)
                throw new BookNotFoundException(title, "Google");

            GoogleBooksEdition edition = response.getItems().get(0).getVolumeInfo();

            return editionToBook(edition);
        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "Google_JSON");
        }
    }

    private Book editionToBook(GoogleBooksEdition edition) {
        List<String> ids = edition.getIndustryIdentifiers().stream()
                .filter((id -> id.containsValue("ISBN_13") || id.containsValue("ISBN_10")))
                .map(id -> id.get("identifier"))
                .collect(Collectors.toList());

        Book.Builder book = new Book.Builder()
                .withTitle(edition.getTitle());

        if (ids.size() > 0)
            book = book.withIsbn10(Isbn10.convert(Isbn.of(ids.get(0))));
        if (edition.getAuthors().size() > 0)
            book = book.withAuthor(edition.getAuthors().get(0));
        if (edition.getDescription() != null && !edition.getDescription().isEmpty())
            book = book.withDescription(edition.getDescription());
        if (edition.getImageLinks() != null && edition.getImageLinks().get("thumbnail") != null)
            book = book.withCoverImage(edition.getImageLinks().get("thumbnail"));

        return book.build();
    }
}
