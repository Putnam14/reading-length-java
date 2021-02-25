package com.readinglength.researcherws.dao.amazon;

import com.amazon.paapi5.v1.Item;
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

    public void queryTitle(String title, Book book) {
        SearchItemsResponse response = amazonDao.queryTitle(title);

        if (response == null) {
            LOG.info("Not found on Amazon");
        }
        if (response != null && response.getSearchResult() != null) {
            Item item = response.getSearchResult().getItems().get(0);
            if (item != null) {
                try {
                    LOG.info(new ObjectMapper().writeValueAsString(item));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                List<String> externalIds = item.getItemInfo().getExternalIds().getIsBNs().getDisplayValues();
                List<Isbn> isbns = externalIds.stream()
                        .filter(Isbn::validate)
                        .map(Isbn::of)
                        .collect(Collectors.toList());
                if (isbns.size() > 0) {
                    if(book.getIsbn10() == null) book.setIsbn10(Isbn10.convert(isbns.get(0)));
                }
            }
        }
    }
}
