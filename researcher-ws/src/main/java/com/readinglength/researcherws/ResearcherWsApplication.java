package com.readinglength.researcherws;

import com.readinglength.researcherws.dao.OpenLibraryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.readinglength")
@RestController
public class ResearcherWsApplication {
	@Autowired
	private OpenLibraryDao olDao;

	public static void main(String[] args) {
		SpringApplication.run(ResearcherWsApplication.class, args);
	}

	@GetMapping("/")
	public ResponseEntity<String> hello() {
		return olDao.queryTitle("A game of thrones");
	}

}
