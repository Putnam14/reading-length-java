package com.readinglength.researcherws;

import com.readinglength.lib.ws.RestClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.readinglength")
@RestController
public class ResearcherWsApplication {
	private static HttpHeaders headers = new HttpHeaders();

	static {
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	public static void main(String[] args) {
		SpringApplication.run(ResearcherWsApplication.class, args);
	}

	@GetMapping("/")
	public ResponseEntity<String> hello() throws IOException {
		RestClient client = new RestClient("http://openlibrary.org/search.json");
		return new ResponseEntity<>(client.get("?q=a+game+of+thrones"), headers, HttpStatus.ACCEPTED);

	}

}
