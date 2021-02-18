package com.readinglength.researcherws.dao.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.readinglength.lib.Isbn;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryWork {
    private Integer coverId;
    private String title;
    private String type;
    private Integer edition_count;
    private String key;
    private String cover_edition_key;
    private Integer first_publish_year;
    private List<String> author_name;
    private List<Integer> publish_year;
    private List<String> subject;
    private List<Isbn> isbn;
    private List<String> edition_key;
    private List<String> language;
    private List<String> id_librarything;
    private List<String> id_goodreads;
    private List<String> lccn;
    private List<String> contributor;
    private List<String> ia;
    private List<String> text;
    private List<String> place;
    private List<String> ddc;
    private List<String> author_key;
    private List<String> id_overdrive;
    private List<String> first_sentence;
    private List<String> oclc;
    private List<String> publisher;
    private List<String> time;
    private List<String> publish_date;


    public Integer getCoverId() {
        return coverId;
    }

    public void setCoverId(Integer cover_i) {
        this.coverId = cover_i;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEdition_count() {
        return edition_count;
    }

    public void setEdition_count(Integer edition_count) {
        this.edition_count = edition_count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCover_edition_key() {
        return cover_edition_key;
    }

    public void setCover_edition_key(String cover_edition_key) {
        this.cover_edition_key = cover_edition_key;
    }

    public Integer getFirst_publish_year() {
        return first_publish_year;
    }

    public void setFirst_publish_year(Integer first_publish_year) {
        this.first_publish_year = first_publish_year;
    }

    public List<String> getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(List<String> author_name) {
        this.author_name = author_name;
    }

    public List<Integer> getPublish_year() {
        return publish_year;
    }

    public void setPublish_year(List<Integer> publish_year) {
        this.publish_year = publish_year;
    }

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public List<Isbn> getIsbn() {
        return isbn;
    }

    public void setIsbn(List<String> isbn) {
        this.isbn = isbn.stream()
                .map(Isbn::of)
                .collect(Collectors.toList());
    }

    public List<String> getEdition_key() {
        return edition_key;
    }

    public void setEdition_key(List<String> edition_key) {
        this.edition_key = edition_key;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<String> getId_librarything() {
        return id_librarything;
    }

    public void setId_librarything(List<String> id_librarything) {
        this.id_librarything = id_librarything;
    }

    public List<String> getId_goodreads() {
        return id_goodreads;
    }

    public void setId_goodreads(List<String> id_goodreads) {
        this.id_goodreads = id_goodreads;
    }

    public List<String> getLccn() {
        return lccn;
    }

    public void setLccn(List<String> lccn) {
        this.lccn = lccn;
    }

    public List<String> getContributor() {
        return contributor;
    }

    public void setContributor(List<String> contributor) {
        this.contributor = contributor;
    }

    public List<String> getIa() {
        return ia;
    }

    public void setIa(List<String> ia) {
        this.ia = ia;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<String> getPlace() {
        return place;
    }

    public void setPlace(List<String> place) {
        this.place = place;
    }

    public List<String> getDdc() {
        return ddc;
    }

    public void setDdc(List<String> ddc) {
        this.ddc = ddc;
    }

    public List<String> getAuthor_key() {
        return author_key;
    }

    public void setAuthor_key(List<String> author_key) {
        this.author_key = author_key;
    }

    public List<String> getId_overdrive() {
        return id_overdrive;
    }

    public void setId_overdrive(List<String> id_overdrive) {
        this.id_overdrive = id_overdrive;
    }

    public List<String> getFirst_sentence() {
        return first_sentence;
    }

    public void setFirst_sentence(List<String> first_sentence) {
        this.first_sentence = first_sentence;
    }

    public List<String> getOclc() {
        return oclc;
    }

    public void setOclc(List<String> oclc) {
        this.oclc = oclc;
    }

    public List<String> getPublisher() {
        return publisher;
    }

    public void setPublisher(List<String> publisher) {
        this.publisher = publisher;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<String> getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(List<String> publish_date) {
        this.publish_date = publish_date;
    }
}
