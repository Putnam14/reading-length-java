package com.readinglength.researcherws.dao.amazon;

import com.amazon.paapi5.v1.ApiClient;
import com.amazon.paapi5.v1.api.DefaultApi;
import com.readinglength.lib.dao.gcp.SecretsDao;

import javax.inject.Singleton;

@Singleton
public class AmazonApiWrapper {
    private DefaultApi api;

    public AmazonApiWrapper() {
        try {
            ApiClient client = new ApiClient();
            String accessKey = SecretsDao.getSecret("AMAZON_API_KEY");
            String privateKey = SecretsDao.getSecret("AMAZON_API_SECRET");
            client.setAccessKey(accessKey);
            client.setSecretKey(privateKey);
            client.setHost("webservices.amazon.com");
            client.setRegion("us-east-1");
            this.api = new DefaultApi(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DefaultApi getApi() {
        return api;
    }

}
