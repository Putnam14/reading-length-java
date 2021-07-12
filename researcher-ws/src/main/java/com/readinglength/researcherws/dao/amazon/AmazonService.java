package com.readinglength.researcherws.dao.amazon;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;

import javax.inject.Inject;
import java.util.List;

public class AmazonService {
    private AmazonDao api;

    @Inject
    public AmazonService(AmazonDao amazonDao) {
        this.api = amazonDao;
    }

    public Book searchKeyword(String keyword) {
        Book.Builder book = new Book.Builder();
        String apiResponse = api.searchItems(keyword);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(apiResponse);
        List<Object> candidates = JsonPath.read(document, "$.SearchResult.Items[?(@.ItemInfo.ExternalIds.ISBNs)]");
        if (candidates.size() > 0) {
            Object result = candidates.get(0);
            Isbn isbn = Isbn.of(JsonPath.read(result, "$.ItemInfo.ExternalIds.ISBNs.DisplayValues[0]"));
            List<String> title = JsonPath.read(result, "$.ItemInfo[?(@.Title)].Title.DisplayValue");
            List<String> author = JsonPath.read(result, "$.ItemInfo.ByLineInfo.Contributors[?(@.RoleType in ['author'])].Name");
            List<String> publishDate = JsonPath.read(result, "$.ItemInfo[?(@.ContentInfo.PublicationDate)].ContentInfo.PublicationDate.DisplayValue");
            List<String> publisher = JsonPath.read(result, "$.ItemInfo[?(@.ByLineInfo.Brand)].ByLineInfo.Brand.DisplayValue");
            List<Integer> pagesCount = JsonPath.read(result, "$.ItemInfo[?(@.ContentInfo.PagesCount)].ContentInfo.PagesCount.DisplayValue");

            book = book.withIsbn10(Isbn10.convert(isbn));
            if (title.size() > 0) book = book.withTitle(title.get(0));
            if (author.size() > 0) book = book.withAuthor(author.get(0));
            if (publishDate.size() > 0) book = book.withPublishDate(publishDate.get(0));
            if (publisher.size() > 0) book = book.withPublisher(publisher.get(0));
            if (pagesCount.size() > 0) book = book.withPagecount(pagesCount.get(0));
        }
        return book.build();
    }
}
