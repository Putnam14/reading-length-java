package com.readinglength.researcherws.dao.openlibrary;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.lib.Isbn13;
import com.readinglength.researcherws.lib.BookNotFoundException;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class OpenLibraryService {
    private final OpenLibraryDao openLibraryDao;

    @Inject
    public OpenLibraryService(OpenLibraryDao openLibraryDao) {
        this.openLibraryDao = openLibraryDao;
    }

    public List<Isbn> queryTitle(String title) throws BookNotFoundException {
        String openLibraryResponse = openLibraryDao.queryTitle(title);

        if (openLibraryResponse == null || "{}".equals(openLibraryResponse))
            throw new BookNotFoundException(title, "OpenLibrary");

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(openLibraryResponse);
        DocumentContext json = JsonPath.parse(document);

        if (((Integer) json.read("$.numFound")) > 0) {
            List<List<String>> potentialIsbns = json.read("$.docs[0][?(@.isbn)].isbn");
            return potentialIsbns.get(0).stream().filter(Isbn::validate).map(Isbn::of).collect(Collectors.toList());
        }

        return List.of();
    }

    public Book queryIsbn(Isbn isbn) throws BookNotFoundException {
        String openLibraryResponse = openLibraryDao.queryIsbn(isbn);

        if (openLibraryResponse == null || "{}".equals(openLibraryResponse))
            throw new BookNotFoundException(isbn.toString(), "OpenLibrary");

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(openLibraryResponse);
        DocumentContext json = JsonPath.parse(document);

        Book.Builder book = new Book.Builder()
                .withIsbn10(Isbn10.convert(isbn))
                .withCoverImage("https://covers.openlibrary.org/b/isbn/" + Isbn13.convert(isbn).toString() + "-L.jpg");

        List<String> title = json.read("$[?(@.title)].title");
        if (title.size() > 0) book = book.withTitle(title.get(0));
        List<Integer> pagecount = json.read("$[?(@.number_of_pages)].number_of_pages");
        if (pagecount.size() > 0) book = book.withPagecount(pagecount.get(0));
        List<String> description = json.read("$[?(@.description)].description");
        if (description.size() > 0) book = book.withDescription(description.get(0));
        List<String> publisher = json.read("$[?(@.publishers)].publishers[0]");
        if (publisher.size() > 0) book = book.withPublisher(publisher.get(0));
        List<String> publishDate = json.read("$[?(@.publish_date)].publish_date");
        if (publishDate.size() > 0) book = book.withPublishDate(publishDate.get(0));

        return book.build();
    }
}
