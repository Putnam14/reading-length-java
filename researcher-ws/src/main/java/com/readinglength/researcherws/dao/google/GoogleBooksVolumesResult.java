package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksVolumesResult {

    List<GoogleBooksItem> items;
    Integer totalItems;

    public List<GoogleBooksItem> getItems() {
        return items;
    }

    public void setItems(List<GoogleBooksItem> items) {
        this.items = items;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}
