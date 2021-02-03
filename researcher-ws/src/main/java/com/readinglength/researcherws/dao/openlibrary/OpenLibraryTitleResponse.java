package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryTitleResponse {
    private List<OpenLibraryWork> docs;


    public List<OpenLibraryWork> getDocs() {
        return docs;
    }

    public void setDocs(List<OpenLibraryWork> docs) {
        this.docs = docs;
    }

}
