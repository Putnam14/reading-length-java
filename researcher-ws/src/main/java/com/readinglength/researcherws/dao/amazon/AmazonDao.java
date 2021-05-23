package com.readinglength.researcherws.dao.amazon;

import com.readinglength.researcherws.lib.BookNotFoundException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class AmazonDao {
    private static final String HOST = "webservices.amazon.com";
    private static final String REGION = "us-east-1";
    private static final String PARTNER_TAG = "readleng-20";

    private String accessKey;
    private String secretKey;

    public AmazonDao(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String searchItems(String keyword) {
        String result = null;
        String requestPayload = String.format("{" +
                        "\"Keywords\":\"%s\"," +
                        "\"PartnerTag\":\"%s\"," +
                        "\"PartnerType\":\"Associates\"," +
                        "\"SearchIndex\":\"Books\"," +
                        "\"Resources\": [" +
                        "  \"ItemInfo.ByLineInfo\"," +
                        "  \"ItemInfo.ContentInfo\"," +
                        "  \"ItemInfo.ExternalIds\"," +
                        "  \"ItemInfo.Title\"," +
                        "  \"Offers.Listings.Price\"," +
                        "  \"Offers.Summaries.LowestPrice\"" +
                        "]}",
                keyword, PARTNER_TAG);
        String amzTarget = "com.amazon.paapi5.v1.ProductAdvertisingAPIv1.SearchItems";
        String path = "/paapi5/searchitems";
        try {
            result = makeRequest(requestPayload, amzTarget, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String makeRequest(String requestPayload, String amzTarget, String path) throws IOException {
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("host", HOST);
        headers.put("content-type", "application/json; charset=utf-8");
        headers.put("x-amz-target", amzTarget);
        headers.put("content-encoding", "amz-1.0");

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("https://" + HOST + path);
        try {
            httpPost.setEntity(new StringEntity(requestPayload));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Signing
        AWSV4Auth auth = auth(headers, requestPayload, path);
        Map<String, String> header = auth.getHeaders();
        for (Map.Entry<String, String> entrySet : header.entrySet()) {
            httpPost.addHeader(entrySet.getKey(), entrySet.getValue());
        }
        try {
            HttpResponse response = client.execute(httpPost);
            if (response != null) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException(String.format("Something went wrong with Amazon request. Response code %d, message: %s", statusCode, body));
                }
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AWSV4Auth auth(TreeMap<String, String> headers, String requestPayload, String path) {
        return new AWSV4Auth.Builder(accessKey, secretKey)
                .path(path)
                .region(REGION)
                .service("ProductAdvertisingAPI")
                .httpMethodName("POST")
                .headers(headers)
                .payload(requestPayload)
                .build();
    }
}
