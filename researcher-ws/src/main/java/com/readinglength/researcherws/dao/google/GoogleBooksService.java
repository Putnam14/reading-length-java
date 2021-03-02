package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class GoogleBooksService {
    private static Logger LOG = LoggerFactory.getLogger(GoogleBooksService.class);
    private final GoogleBooksDao googleBooksDao;
    private final ObjectMapper objectMapper;

    @Inject
    public GoogleBooksService(GoogleBooksDao googleBooksDao, ObjectMapper objectMapper) {
        this.googleBooksDao = googleBooksDao;
        this.objectMapper = objectMapper;
    }

    public Book queryTitle(String title) throws BookNotFoundException {
        return processResponse(googleBooksDao.queryTitle(title), title);
    }

    public Book queryIsbn(Isbn isbn) throws BookNotFoundException {
        return processResponse(googleBooksDao.queryIsbn(isbn), isbn.getIsbn());
    }

    private Book processResponse(String googleBooksResponse, String title) throws BookNotFoundException  {
        if (googleBooksResponse == null || "{}".equals(googleBooksResponse))
            throw new BookNotFoundException(title, "Google");

        Book book = new Book();

        try {
            GoogleBooksVolumesResult response = objectMapper.readValue(googleBooksResponse, GoogleBooksVolumesResult.class);

            if (response.getTotalItems() == 0)
                throw new BookNotFoundException(title, "Google");

            GoogleBooksEdition edition = response.getItems().get(0).getVolumeInfo();
            LOG.info(new ObjectMapper().writeValueAsString(edition));
            List<String> ids = edition.getIndustryIdentifiers().stream()
                    .filter((id -> id.containsValue("ISBN_13") || id.containsValue("ISBN_10")))
                    .map(id -> id.get("identifier"))
                    .collect(Collectors.toList());
            if (ids.size() > 0)
                book.setIsbn10(Isbn10.convert(Isbn.of(ids.get(0))));
            if (edition.getAuthors().size() > 0)
                book.setAuthor(edition.getAuthors().get(0));
            if (edition.getDescription() != null && !edition.getDescription().isEmpty())
                book.setDescription(edition.getDescription());
            if (edition.getImageLinks() != null && edition.getImageLinks().get("thumbnail") != null)
                book.setCoverImage(edition.getImageLinks().get("thumbnail"));
            book.setTitle(edition.getTitle());

        } catch(JsonProcessingException e) {
            throw new BookNotFoundException(title, "Google_JSON");
        }

        return book;
    }
}
