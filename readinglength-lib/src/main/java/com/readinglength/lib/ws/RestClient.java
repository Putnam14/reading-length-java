package com.readinglength.lib.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RestClient {
    private String serverBaseUri;
    private RestTemplate restTemplate;
    private HttpEntity<String> httpEntity;
    public static HttpHeaders JSON_HEADERS = new HttpHeaders();
    private static Logger LOG = LoggerFactory.getLogger(RestClient.class);

    static {
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    public RestClient(RestTemplate restTemplate, HttpEntity<String> httpEntity) {
        this.restTemplate = restTemplate;
        this.httpEntity = httpEntity;
    }

    public void setServerBaseUri(String serverBaseUri) {
        this.serverBaseUri = serverBaseUri;
    }

    public String get(String endpoint, Map<String, String> queryParams) {
        String requestString = queryParamsToRequestString(endpoint, queryParams);
        return makeGetRequest(requestString);
    }

    public String get(String endpoint, String request) {
        String requestString = serverBaseUri + endpoint + request;
        return makeGetRequest(requestString);
    }

    private String makeGetRequest(String requestString) {
        if(serverBaseUri == null) throw new IllegalStateException("Server URI is null.");
        LOG.info(String.format("Making GET request to: %s", requestString));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestString, HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();

    }

    private String queryParamsToRequestString(String endpoint, Map<String, String> queryParams) {
        if(serverBaseUri == null) throw new IllegalStateException("Server URI is null.");
        StringBuilder queryString = new StringBuilder(serverBaseUri);

        queryString.append(String.format("%s?", endpoint));
        queryParams.forEach((k, v) -> queryString
                    .append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                    .append('&'));
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
    }

}
