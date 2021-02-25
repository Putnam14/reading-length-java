package com.readinglength.researcherws.dao.google;

import com.readinglength.lib.Isbn;
import com.readinglength.lib.ws.RestClient;
import com.readinglength.researcherws.dao.gcp.SecretsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class GoogleBooksDao {
    private static Logger LOG = LoggerFactory.getLogger(GoogleBooksDao.class);
    private String apiKey;

    private RestClient restClient;

    public GoogleBooksDao() {
        try {
            this.apiKey = SecretsDao.getSecret("GOOGLE_BOOKS_API_KEY");
            RestClient restClient = new RestClient();
            restClient.setClient(new URL("https://www.googleapis.com/books/v1/"));
            this.restClient = restClient;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

    }

    public String queryTitle(String title) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("q", title);
        queryParams.put("printType", "BOOKS");
        queryParams.put("key", apiKey);
        return restClient.get("volumes", queryParams);
    }

    public String queryIsbn(Isbn isbn) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("q", String.format("isbn:%s", isbn.getIsbn()));
        queryParams.put("key", apiKey);
        return restClient.get("volumes", queryParams);
    }

}
