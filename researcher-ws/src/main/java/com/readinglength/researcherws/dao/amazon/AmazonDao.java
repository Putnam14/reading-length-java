package com.readinglength.researcherws.dao.amazon;

import com.amazon.paapi5.v1.ApiException;
import com.amazon.paapi5.v1.PartnerType;
import com.amazon.paapi5.v1.SearchItemsRequest;
import com.amazon.paapi5.v1.SearchItemsResource;
import com.amazon.paapi5.v1.SearchItemsResponse;
import com.amazon.paapi5.v1.api.DefaultApi;
import com.readinglength.researcherws.dao.gcp.SecretsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AmazonDao {
    private final DefaultApi api;
    private static String partnerTag = "readleng-20";
    private static Logger LOG = LoggerFactory.getLogger(AmazonDao.class);

    static {
        try {
            partnerTag = SecretsDao.getSecret("AMAZON_AFFILIATE_TAG");
        } catch (Exception e) {
            LOG.error(String.format("AmazonDao unavailable: %s", e.getMessage()));
        }
    }

    @Inject
    public AmazonDao(AmazonApiWrapper amazonApiWrapper) {
        this.api = amazonApiWrapper.getApi();
    }

    public SearchItemsResponse queryTitle(String title, List<SearchItemsResource> searchItemsResources) {
        return searchBooks(title, searchItemsResources);
    }

    private SearchItemsResponse searchBooks(String keywords, List<SearchItemsResource> searchItemsResources) {
        String searchIndex = "Books";

        SearchItemsRequest request =  new SearchItemsRequest().partnerTag(partnerTag).keywords(keywords)
                .searchIndex(searchIndex).resources(searchItemsResources).partnerType(PartnerType.ASSOCIATES);

        SearchItemsResponse response = null;

        synchronized(api) {
            try {
                response = api.searchItems(request);
            } catch (ApiException e) {
                LOG.error(String.format("Error calling Amazon: %s", e.getMessage()));
            }
        }
        return response;
    }
}
