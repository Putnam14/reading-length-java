package com.readinglength.researcherws;


import com.readinglength.researcherws.controller.Search;
import io.javalin.Javalin;

public class ResearcherWsApplication {
	public static void main(String[] args) {
		ResearcherComponent researcherComponent = DaggerResearcherComponent.create();
		Search search = researcherComponent.search();
		Javalin app = Javalin.create().start(8080);
		app.get("/", search.index);
		app.get("/byTitle", search.byTitle);
		app.get("/byIsbn", search.byIsbn);
	}
}
