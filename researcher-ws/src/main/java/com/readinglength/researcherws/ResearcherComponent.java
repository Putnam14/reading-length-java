package com.readinglength.researcherws;

import com.readinglength.researcherws.controller.Search;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ResearcherModule.class)
public interface ResearcherComponent {
    Search search();
}
