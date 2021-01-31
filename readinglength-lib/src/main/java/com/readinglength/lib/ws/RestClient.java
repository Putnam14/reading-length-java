package com.readinglength.lib.ws;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RestClient {
    private String serverBaseUri;
    private RestTemplate restTemplate;
    private HttpEntity<String> httpEntity;

    public RestClient(RestTemplate restTemplate, HttpEntity<String> httpEntity) {
        this.restTemplate = restTemplate;
        this.httpEntity = httpEntity;
    }

    public void setServerBaseUri(String serverBaseUri) {
        this.serverBaseUri = serverBaseUri;
    }


    public String get(Map<String, String> queryParams) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(queryParamsToRequestString(queryParams), HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();
    }

    private String queryParamsToRequestString(Map<String, String> queryParams) {
        if(serverBaseUri == null) throw new IllegalStateException("Server URI is null.");

        StringBuilder queryString = new StringBuilder(serverBaseUri);

        queryString.append('?');
        queryParams.forEach((k, v) -> queryString
                    .append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                    .append('&'));
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
    }

}
