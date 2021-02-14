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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AmazonDao {
    private DefaultApi api;
    private static String partnerTag = "readleng-20";
    private static Logger LOG = LoggerFactory.getLogger(AmazonDao.class);

    static {
        try {
            partnerTag = SecretsDao.getSecret("AMAZON_AFFILIATE_TAG");
        } catch (Exception e) {
            LOG.error(String.format("AmazonDao unavailable: %s", e.getMessage()));
        }
    }

    @Autowired
    public AmazonDao(DefaultApi amazonApi) {
        this.api = amazonApi;
    }

    public SearchItemsResponse queryTitle(String title) {
        return searchItems(title);
    }

    private List<SearchItemsResource> initializeRequest() {
        List<SearchItemsResource> searchItemsResources = new ArrayList<>();
        searchItemsResources.add(SearchItemsResource.ITEMINFO_EXTERNALIDS);
        return searchItemsResources;
    }

    private SearchItemsResponse searchItems(String keywords) {
        String searchIndex = "Books";

        List<SearchItemsResource> searchItemsResources = initializeRequest();

        SearchItemsRequest request =  new SearchItemsRequest().partnerTag(partnerTag).keywords(keywords)
                .searchIndex(searchIndex).resources(searchItemsResources).partnerType(PartnerType.ASSOCIATES);

        SearchItemsResponse response = null;

        try {
            response = api.searchItems(request);
        } catch (ApiException e) {
            // Exception handling
            System.out.println("Error calling PA-API 5.0!");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Errors: " + e.getResponseBody());
            System.out.println("Message: " + e.getMessage());
            if (e.getResponseHeaders() != null) {
                // Printing request reference
                System.out.println("Request ID: " + e.getResponseHeaders().get("x-amzn-RequestId"));
            }
        }
        return response;
    }
}
