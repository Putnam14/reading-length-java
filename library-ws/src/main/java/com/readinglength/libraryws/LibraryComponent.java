package com.readinglength.libraryws;

import com.readinglength.libraryws.controller.Query;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component
public interface LibraryComponent {
    Query query();
}