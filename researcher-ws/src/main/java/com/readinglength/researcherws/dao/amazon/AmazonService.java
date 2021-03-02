package com.readinglength.researcherws.dao.amazon;

import com.amazon.paapi5.v1.Contributor;
import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.ItemInfo;
import com.amazon.paapi5.v1.SearchItemsResource;
import com.amazon.paapi5.v1.SearchItemsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.Book;
import com.readinglength.lib.Isbn;
import com.readinglength.lib.Isbn10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AmazonService {

    private static Logger LOG = LoggerFactory.getLogger(AmazonService.class);

    private final AmazonDao amazonDao;

    @Inject
    public AmazonService(AmazonDao amazonDao) {
        this.amazonDao = amazonDao;
    }

    public Book queryTitle(String query) {
        SearchItemsResponse response = amazonDao.queryTitle(query, List.of(
                SearchItemsResource.ITEMINFO_EXTERNALIDS,
                SearchItemsResource.ITEMINFO_TITLE,
                SearchItemsResource.ITEMINFO_BYLINEINFO,
                SearchItemsResource.ITEMINFO_CONTENTINFO));
        Book book = new Book();

        if (response == null)
            LOG.info("Not found on Amazon");
        else if (response.getSearchResult() != null) {
            Item item = response.getSearchResult().getItems().get(0);
            if (item != null) {
                try {
                    LOG.info("Amazon result");
                    LOG.info(new ObjectMapper().writeValueAsString(item));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                ItemInfo info = item.getItemInfo();

                if (info.getExternalIds() != null) {
                    List<String> externalIds = info.getExternalIds().getIsBNs().getDisplayValues();
                    List<Isbn> isbns = externalIds.stream()
                            .filter(Isbn::validate)
                            .map(Isbn::of)
                            .collect(Collectors.toList());
                    book.setIsbn10(Isbn10.convert(isbns.get(0)));
                }

                Contributor author = info.getByLineInfo().getContributors().stream()
                        .filter(c -> "author".equals(c.getRoleType()))
                        .findFirst()
                        .orElse(null);

                if (book.getIsbn10() == null)
                    book.setIsbn10(new Isbn10(item.getASIN()));

                book.setTitle(info.getTitle().getDisplayValue());
                book.setAuthor(author != null ? author.getName() : null);
                book.setPublishDate(info.getContentInfo().getPublicationDate().getDisplayValue());
                book.setPublisher(info.getByLineInfo().getBrand().getDisplayValue());
                book.setPagecount(info.getContentInfo().getPagesCount().getDisplayValue());
            }
        }
        return book;
    }
}
