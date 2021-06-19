package com.readinglength.libraryws.dao;

import com.readinglength.libraryws.auth.Authentication;

import javax.inject.Inject;

public class ResearcherDao {
    private Authentication auth;

    @Inject
    public ResearcherDao(Authentication auth) {
        this.auth = auth;
    }
}
