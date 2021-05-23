package com.readinglength.researcherws.controller;

import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import com.readinglength.lib.Isbn13;
import com.readinglength.researcherws.dao.amazon.AmazonService;
import com.readinglength.researcherws.dao.google.GoogleBooksService;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryService;
import com.readinglength.researcherws.lib.BookNotFoundException;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchTest {
    private static Book expectedBook;
    private OpenLibraryService openLibraryService;
    private GoogleBooksService googleBooksService;
    private AmazonService amazonService;
    private Search instance;
    private Context ctx;


    @BeforeAll
    static void setUpAll() {
        expectedBook = new Book.Builder()
                .withIsbn10(new Isbn10("0307279464"))
                .withTitle("A Walk in the Woods")
                .withAuthor("Bill Bryson")
                .withPublishDate("2006-12-26")
                .withPublisher("Anchor")
                .withPagecount(416)
                .withCoverImage("https://covers.openlibrary.org/b/isbn/9780307279460-L.jpg")
                .withDescription("Traces the author's adventurous trek along the Appalachian Trail past its natural pleasures, human eccentrics, and offbeat comforts.")
                .build();
    }

    @BeforeEach
    void setUp() throws IOException {
        openLibraryService = mock(OpenLibraryService.class);
        amazonService = mock(AmazonService.class);
        googleBooksService = mock(GoogleBooksService.class);
        instance = new Search(openLibraryService, amazonService, googleBooksService);
    }

    @Test
    void byTitle() throws BookNotFoundException {
        String keyword = "a walk in the woods";
        Isbn isbn = Isbn.of("0307279464");
        ctx = mock(Context.class);
        when(ctx.queryParam("title")).thenReturn(keyword);
        when(openLibraryService.queryTitle(keyword)).thenReturn(List.of(isbn, Isbn.of("1400025117"), Isbn.of("9780767902526")));
        when(openLibraryService.queryIsbn(isbn)).thenReturn(new Book.Builder()
                .withTitle("A Walk in the Woods")
                .withIsbn10(Isbn10.convert(isbn))
                .withIsbn13(Isbn13.convert(isbn))
                .withPublisher("Anchor")
                .withPublishDate("2006-12-26")
                .withCoverImage("https://covers.openlibrary.org/b/isbn/9780307279460-L.jpg")
                .withPagecount(416)
                .build());
        when(amazonService.searchKeyword(keyword)).thenReturn(new Book.Builder()
                .withIsbn10(Isbn10.convert(isbn))
                .withTitle("A Walk in the Woods: Rediscovering America on the Appalachian Trail")
                .withAuthor("Bryson, Bill")
                .withPublishDate("2006-12-26")
                .withPublisher("Anchor")
                .withPagecount(397)
                .build());
        when(googleBooksService.queryIsbn(isbn)).thenReturn(new Book.Builder()
                .withTitle("A Walk in the Woods")
                .withIsbn10(new Isbn10("0307279464"))
                .withAuthor("Bill Bryson")
                .withDescription("Traces the author's adventurous trek along the Appalachian Trail past its natural pleasures, human eccentrics, and offbeat comforts.")
                .withCoverImage("http://books.google.com/books/content?id=TBJHPgAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
                .build());

        instance.byTitle(ctx);

        verify(ctx).json(expectedBook);
    }

    @Test
    void byIsbn() throws BookNotFoundException {
        String isbn = "0307279464";
        ctx = mock(Context.class);
        when(ctx.queryParam("isbn")).thenReturn(isbn);
        when(openLibraryService.queryIsbn(Isbn.of(isbn))).thenReturn(new Book.Builder()
                .withTitle("A Walk in the Woods")
                .withIsbn10(new Isbn10(isbn))
                .withPublisher("Anchor")
                .withPublishDate("2006-12-26")
                .withCoverImage("https://covers.openlibrary.org/b/isbn/9780307279460-L.jpg")
                .withPagecount(416)
                .build());
        when(googleBooksService.queryIsbn(Isbn.of(isbn))).thenReturn(new Book.Builder()
                .withTitle("A Walk in the Woods")
                .withIsbn10(new Isbn10("0307279464"))
                .withAuthor("Bill Bryson")
                .withDescription("Traces the author's adventurous trek along the Appalachian Trail past its natural pleasures, human eccentrics, and offbeat comforts.")
                .withCoverImage("http://books.google.com/books/content?id=TBJHPgAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
                .build());

        instance.byIsbn(ctx);

        verify(ctx).json(expectedBook);
    }
}