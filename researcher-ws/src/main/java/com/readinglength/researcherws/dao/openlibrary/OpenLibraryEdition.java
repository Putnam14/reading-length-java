package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryEdition {
    private List<String> publishers;
    private Integer number_of_pages;
    private String subtitle;
    private Map<String, String> last_modified;
    private List<String> source_records;
    private String title;
    private Map<String,List<Integer>> identifiers;
    private List<String> isbn_13;
    private List<Integer> covers;
    private Map<String, String> created;
    private String physical_format;
    private List<String> isbn_10;
    private String publish_date;
    private String key;
    private List<Map<String, String>> authors;
    private Integer latest_revision;
    private List<Map<String, String>> works;
    private Map<String, String> type;
    private Integer revision;
    private List<Map<String, String>> languages;

    public Integer getNumber_of_pages() {
        return number_of_pages;
    }

    public void setNumber_of_pages(Integer number_of_pages) {
        this.number_of_pages = number_of_pages;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Map<String, String>> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Map<String, String>> languages) {
        this.languages = languages;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public Map<String, String> getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Map<String, String> last_modified) {
        this.last_modified = last_modified;
    }

    public List<String> getSource_records() {
        return source_records;
    }

    public void setSource_records(List<String> source_records) {
        this.source_records = source_records;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, List<Integer>> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, List<Integer>> identifiers) {
        this.identifiers = identifiers;
    }

    public List<String> getIsbn_13() {
        return isbn_13;
    }

    public void setIsbn_13(List<String> isbn_13) {
        this.isbn_13 = isbn_13;
    }

    public List<Integer> getCovers() {
        return covers;
    }

    public void setCovers(List<Integer> covers) {
        this.covers = covers;
    }

    public Map<String, String> getCreated() {
        return created;
    }

    public void setCreated(Map<String, String> created) {
        this.created = created;
    }

    public String getPhysical_format() {
        return physical_format;
    }

    public void setPhysical_format(String physical_format) {
        this.physical_format = physical_format;
    }

    public List<String> getIsbn_10() {
        return isbn_10;
    }

    public void setIsbn_10(List<String> isbn_10) {
        this.isbn_10 = isbn_10;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Map<String, String>> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Map<String, String>> authors) {
        this.authors = authors;
    }

    public Integer getLatest_revision() {
        return latest_revision;
    }

    public void setLatest_revision(Integer latest_revision) {
        this.latest_revision = latest_revision;
    }

    public List<Map<String, String>> getWorks() {
        return works;
    }

    public void setWorks(List<Map<String, String>> works) {
        this.works = works;
    }

    public Map<String, String> getType() {
        return type;
    }

    public void setType(Map<String, String> type) {
        this.type = type;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

}
