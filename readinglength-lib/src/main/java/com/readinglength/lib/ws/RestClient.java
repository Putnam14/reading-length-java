package com.readinglength.lib.ws;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RestClient {
    private String baseUri;
    private HttpClient httpClient;
    private static Logger LOG = LoggerFactory.getLogger(RestClient.class);

    public RestClient() { }

    public void setClient(URL serverBaseUri) {
        this.baseUri = serverBaseUri.toString();
        this.httpClient = HttpClientBuilder.create().build();
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
        HttpGet httpGet = new HttpGet(baseUri + requestString);
        HttpResponse response = null;
        try {
             response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = 0;
        if (response != null) {
            statusCode = response.getStatusLine().getStatusCode();
        }
        if (statusCode != 200) {
            LOG.debug("Non-200 response: " + response);
            return "{}";
        }
        HttpEntity entity = response.getEntity();
        String jsonResponse = null;
        try {
            jsonResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;

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
