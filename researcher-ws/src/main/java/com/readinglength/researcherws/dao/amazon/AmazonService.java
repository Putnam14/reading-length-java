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
        Book book = new Book();
        try {
            String apiResponse = api.searchItems(keyword);
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(apiResponse);
            List<Object> candidates = JsonPath.read(document, "$.SearchResult.Items[?(@.ItemInfo.ExternalIds.ISBNs)]");
            if (candidates.size() > 0) {
                Object result = candidates.get(0);
                Isbn isbn = Isbn.of(JsonPath.read(result, "$.ItemInfo.ExternalIds.ISBNs.DisplayValues[0]"));
                List<String> title = JsonPath.read(result, "$.[?(@.ItemInfo.Title)].ItemInfo.Title.DisplayValue");
                List<String> author = JsonPath.read(result, "$.[?(@.ItemInfo.ByLineInfo.Contributors[?(@.RoleType =~ /.author/i)])].ItemInfo.ByLineInfo.Contributors[0].Name");
                List<String> publishDate = JsonPath.read(result, "$.[?(@.ItemInfo.ContentInfo.PublicationDate)].ItemInfo.ContentInfo.PublicationDate.DisplayValue");
                List<String> publisher = JsonPath.read(result, "$.[?(@.ItemInfo.ByLineInfo.Brand)].ItemInfo.ByLineInfo.Brand.DisplayValue");
                List<Integer> pagesCount = JsonPath.read(result, "$.[?(@.ItemInfo.ContentInfo.PagesCount)].ItemInfo.ContentInfo.PagesCount.DisplayValue");
                book.setIsbn10(Isbn10.convert(isbn));
                if (title.size() > 0) book.setTitle(title.get(0));
                if (author.size() > 0) book.setAuthor(author.get(0));
                if (publishDate.size() > 0) book.setPublishDate(publishDate.get(0));
                if (publisher.size() > 0) book.setPublisher(publisher.get(0));
                if (pagesCount.size() > 0) book.setPagecount(pagesCount.get(0));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return book;
    }
}
