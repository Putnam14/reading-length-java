package com.readinglength.lib;

import com.github.sisyphsu.dateparser.DateParserUtils;

import java.time.LocalDate;
import java.time.ZoneId;

public class Book {
    private String title;
    private String author;
    private String description;
    private Isbn10 isbn10;
    private Isbn13 isbn13;
    private Integer pagecount;
    private String coverImage;
    private String publisher;
    private LocalDate publishDate;
    private Wordcount wordcount;

    public void merge(Book toMerge) {
        if (title == null) setTitle(toMerge.getTitle());
        if (author == null) setAuthor(toMerge.getAuthor());
        if (description == null) setDescription(toMerge.getDescription());
        if (isbn10 == null && toMerge.getIsbn10() != null) setIsbn10(toMerge.getIsbn10());
        if (pagecount == null) setPagecount(toMerge.getPagecount());
        if (coverImage == null) setCoverImage(toMerge.getCoverImage());
        if (publisher == null) setPublisher(toMerge.getPublisher());
        if (publishDate == null) setPublishDate(toMerge.getPublishDate());
        if (wordcount == null) setWordcount(toMerge.getWordcount());
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Isbn10 getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(Isbn10 isbn10) {
        this.isbn10 = isbn10;
        this.isbn13 = Isbn13.convert(isbn10);
    }

    public Isbn13 getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(Isbn13 isbn13) {
        this.isbn13 = isbn13;
        this.isbn10 = Isbn10.convert(isbn13);
    }

    public Integer getPagecount() {
        return pagecount;
    }

    public void setPagecount(Integer pagecount) {
        this.pagecount = pagecount;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPublishDate() {
        return publishDate.toString();
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = DateParserUtils.parseDateTime(publishDate).toLocalDate();
    }

    public Wordcount getWordcount() {
        return wordcount;
    }

    public void setWordcount(Wordcount wordcount) {
        this.wordcount = wordcount;
    }
}
