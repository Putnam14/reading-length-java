package com.readinglength.researcherws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class ResearcherWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResearcherWsApplication.class, args);
	}

	@RestController
	class HelloWorldController {
		@GetMapping("/")
		public ResponseEntity<String> hello() throws IOException {
			RestClient client = new RestClient();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity<>(client.get("?q=a+game+of+thrones"), headers, HttpStatus.ACCEPTED);

		}

	}

	public class RestClient {

		private String server = "http://openlibrary.org/search.json";
		private RestTemplate rest;
		private HttpHeaders headers;

		RestClient() {
			this.rest = new RestTemplate();
			this.headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/json");
		}

		String get(String uri) {
			HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
			ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.GET, requestEntity, String.class);
			return responseEntity.getBody();
		}
	}

}
