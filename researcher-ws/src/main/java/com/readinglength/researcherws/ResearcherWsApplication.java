package com.readinglength.researcherws;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.researcherws.controller.Search;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ResearcherWsApplication {
	public static void main(String[] args) {
		ResearcherComponent researcherComponent = DaggerResearcherComponent.create();
		Search search = researcherComponent.search();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		JavalinJackson.configure(objectMapper);

		Javalin app = Javalin.create().start(8080);

		app.routes(() -> {
			path("/", () -> get(search::index));
			path("/byTitle", () -> get(search::byTitle));
			path("/byIsbn", () -> get(search::byIsbn));
		});
	}
}
