package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksVolumesResult {

    List<GoogleBooksItem> items;

    public List<GoogleBooksItem> getItems() {
        return items;
    }

    public void setItems(List<GoogleBooksItem> items) {
        this.items = items;
    }
}
