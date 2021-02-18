package com.readinglength.lib.ws;


import io.micronaut.http.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestClientTest {
    private RestClient restClient;

//
//    @BeforeEach
//    void setUpEach() {
//        restClient = new RestClient();
//        when(mockResponse.getBody()).thenReturn("Success");
//    }
//
//
//    @Test
//    void testIllegalState() {
//        assertThrows(IllegalStateException.class, () -> restClient.get("/", Map.of()));
//    }
//
//    @Test
//    void testQueryParams() {
//        when(mockRestTemplate
//                .exchange(
//                        "example.com/test?key1=value1&key2=value2+is+a+sentence",
//                        HttpMethod.GET,
//                        httpEntity,
//                        String.class))
//                .thenReturn(mockResponse);
//        restClient.setServerBaseUri("example.com/");
//
//        Map<String, String> queryParams = new LinkedHashMap<>();
//        queryParams.put("key1","value1");
//        queryParams.put("key2","value2 is a sentence");
//
//        assertEquals(mockResponse.getBody(), restClient.get("test", queryParams));
//    }
//
//    @Test
//    void testGetEndpoint() {
//        when(mockRestTemplate
//                .exchange(
//                        "example.com/test/value.json",
//                        HttpMethod.GET,
//                        httpEntity,
//                        String.class))
//                .thenReturn(mockResponse);
//
//        restClient.setServerBaseUri("example.com/");
//
//        assertEquals(mockResponse.getBody(), restClient.get("test/", "value.json"));
//    }


}