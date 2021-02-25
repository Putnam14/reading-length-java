package com.readinglength.researcherws.dao.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksEdition {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Map<String, String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(Map<String, String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private List<Map<String, String>> industryIdentifiers;
    private Integer pageCount;
    private Map<String, String> imageLinks;


    public List<Map<String, String>> getIndustryIdentifiers() {
        return industryIdentifiers;
    }

    public void setIndustryIdentifiers(List<Map<String, String>> industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
    }
}
