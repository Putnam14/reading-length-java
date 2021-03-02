package com.readinglength.lib.ws;

import io.micronaut.http.client.RxHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RestClient {
    private String baseUri;
    private RxHttpClient httpClient;
    private static Logger LOG = LoggerFactory.getLogger(RestClient.class);

    public RestClient() { }

    public void setClient(URL serverBaseUri) {
        this.baseUri = serverBaseUri.toString();
        this.httpClient = RxHttpClient.create(serverBaseUri);
    }


    public String get(String endpoint, Map<String, String> queryParams) {
        String requestString = queryParamsToRequestString(endpoint, queryParams);
        return makeGetRequest(requestString);
    }

    public String get(String endpoint, String request) {
        String requestString = endpoint + request;
        return makeGetRequest(requestString);
    }

    private String makeGetRequest(String requestString) {
        LOG.info(String.format("Making GET request to: %s", baseUri + requestString));
        return httpClient.retrieve(requestString).onErrorReturnItem("{}").blockingSingle();

    }

    private String queryParamsToRequestString(String endpoint, Map<String, String> queryParams) {
        StringBuilder queryString = new StringBuilder();

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
