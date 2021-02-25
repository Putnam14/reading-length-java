package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.readinglength.researcherws.dao.google.GoogleBooksEdition;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksItem {
    private GoogleBooksEdition  volumeInfo;

    public GoogleBooksEdition getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(GoogleBooksEdition volumeInfo) {
        this.volumeInfo = volumeInfo;
    }
}
