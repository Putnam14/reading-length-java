package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
