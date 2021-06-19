package com.readinglength.libraryws.dao;

import com.readinglength.libraryws.auth.Authentication;

import javax.inject.Inject;

public class ArchivistDao {
    private Authentication auth;

    @Inject
    public ArchivistDao(Authentication auth) {
        this.auth = auth;
    }
}
