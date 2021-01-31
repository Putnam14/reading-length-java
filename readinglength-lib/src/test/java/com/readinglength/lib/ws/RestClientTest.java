package com.readinglength.lib.ws;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RestClientTest {
    private static HttpEntity<String> httpEntity;
    private RestTemplate mockRestTemplate;
    private RestClient restClient;

    @BeforeAll
    static void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpEntity = new HttpEntity<>("", headers);
    }

    @BeforeEach
    void setUpEach() {
        mockRestTemplate = mock(RestTemplate.class);
        restClient = new RestClient(mockRestTemplate, httpEntity);
    }


    @Test
    void testIllegalState() {
        assertThrows(IllegalStateException.class, () -> restClient.get(Map.of()));
    }

}