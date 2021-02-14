package com.readinglength.researcherws.dao.amazon;

import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.SearchItemsResponse;
import com.readinglength.lib.Isbn;
import com.readinglength.researcherws.lib.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AmazonService {

    private final AmazonDao amazonDao;

    @Autowired
    public AmazonService(AmazonDao amazonDao) {
        this.amazonDao = amazonDao;
    }

    public Isbn queryTitle(String title) throws BookNotFoundException {

        Isbn isbn = null;

        SearchItemsResponse response = amazonDao.queryTitle(title);

        if (response != null && response.getSearchResult() != null) {
            Item item = response.getSearchResult().getItems().get(0);
            if (item != null) {
                List<String> externalIds = item.getItemInfo().getExternalIds().getIsBNs().getDisplayValues();
                List<Isbn> isbns = externalIds.stream()
                        .filter(Isbn::validate)
                        .map(Isbn::of)
                        .collect(Collectors.toList());
                if (isbns.size() > 0) {
                    isbn = isbns.get(0);
                }
            }
        }

        if (isbn == null) throw new BookNotFoundException(title, "Amazon");

        return isbn;
    }
}
