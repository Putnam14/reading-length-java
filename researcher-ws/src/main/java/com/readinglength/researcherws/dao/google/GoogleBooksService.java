package com.readinglength.researcherws.dao.google;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.researcherws.lib.BookNotFoundException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GoogleBooksService {
    private final GoogleBooksDao googleBooksDao;

    @Inject
    public GoogleBooksService(GoogleBooksDao googleBooksDao) {
        this.googleBooksDao = googleBooksDao;
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

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(googleBooksResponse);
        List<Object> items = JsonPath.read(document, "$.items");
        if (items.size() == 0)
            throw new BookNotFoundException(title, "Google");

        return editionToBook(items.get(0));
    }

    private Book editionToBook(Object edition) {
        Book.Builder book = new Book.Builder();
        DocumentContext json = JsonPath.parse(edition);

        List<String> title = json.read("$.volumeInfo[?(@.title)].title");
        if (title.size() > 0) book = book.withTitle(title.get(0));
        List<String> isbn = json.limit(1).read("$.volumeInfo.industryIdentifiers[?(@.type in ['ISBN_10', 'ISBN_13'])].identifier");
        if (isbn.size() > 0) book = book.withIsbn10(Isbn10.convert(Isbn.of(isbn.get(0))));
        List<String> author = json.read("$.volumeInfo[?(@.authors)].authors[0]");
        if (author.size() > 0) book = book.withAuthor(author.get(0));
        List<String> description = json.read("$.volumeInfo[?(@.description)].description");
        if (description.size() > 0) book = book.withDescription(description.get(0));
        List<String> publisher = json.read("$.volumeInfo[?(@.publisher)].publisher");
        if (publisher.size() > 0) book = book.withPublisher(publisher.get(0));
        List<String> publishDate = json.read("$.volumeInfo[?(@.publishedDate)].publishedDate");
        if (publishDate.size() > 0) book = book.withPublishDate(publishDate.get(0));
        List<Integer> pagecount = json.read("$.volumeInfo[?(@.pageCount)].pageCount");
        if (pagecount.size() > 0) book = book.withPagecount(pagecount.get(0));
        List<String> imageLink = json.read("$.volumeInfo[?(@.imageLinks.thumbnail)].imageLinks.thumbnail");
        if (imageLink.size() > 0) book = book.withCoverImage(imageLink.get(0));

        return book.build();
    }
}
