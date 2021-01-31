package com.readinglength.lib.ws;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestClientTest {
    private static HttpEntity<String> httpEntity;
    private RestClient restClient;

    private RestTemplate mockRestTemplate;
    private ResponseEntity<String> mockResponse;

    @BeforeAll
    static void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpEntity = new HttpEntity<>("", headers);
    }

    @BeforeEach
    void setUpEach() {
        mockResponse = (ResponseEntity<String>) mock(ResponseEntity.class);
        mockRestTemplate = mock(RestTemplate.class);
        restClient = new RestClient(mockRestTemplate, httpEntity);
        when(mockResponse.getBody()).thenReturn("Success");
    }


    @Test
    void testIllegalState() {
        assertThrows(IllegalStateException.class, () -> restClient.get(Map.of()));
    }

    @Test
    void testQueryParams() {
        when(mockRestTemplate
                .exchange(
                    "test?key1=value1&key2=value2+is+a+sentence",
                    HttpMethod.GET,
                    httpEntity,
                    String.class))
                .thenReturn(mockResponse);
        restClient.setServerBaseUri("test");
        
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("key1","value1");
        queryParams.put("key2","value2 is a sentence");

        assertEquals(mockResponse.getBody(), restClient.get(queryParams));
    }


}