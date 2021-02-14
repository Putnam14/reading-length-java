package com.readinglength.researcherws;

import com.amazon.paapi5.v1.ApiClient;
import com.amazon.paapi5.v1.api.DefaultApi;
import com.readinglength.lib.ws.RestClient;
import com.readinglength.researcherws.dao.gcp.SecretsDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        return new RestClient(restTemplate, httpEntity);
    }

    @Bean
    public DefaultApi amazonApi() {
        DefaultApi api = null;
        try {
            ApiClient client = new ApiClient();
            String accessKey = SecretsDao.getSecret("AMAZON_API_KEY");
            String privateKey = SecretsDao.getSecret("AMAZON_API_SECRET");
            client.setAccessKey(accessKey);
            client.setSecretKey(privateKey);
            client.setHost("webservices.amazon.com");
            client.setRegion("us-east-1");
            api = new DefaultApi(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return api;
    }

}
