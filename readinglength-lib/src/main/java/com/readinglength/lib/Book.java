package com.readinglength.lib;

public class Book {
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
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Wordcount getWordcount() {
        return wordcount;
    }

    public void setWordcount(Wordcount wordcount) {
        this.wordcount = wordcount;
    }

    private String title;
    private String author;
    private String description;
    private Isbn10 isbn10;
    private Isbn13 isbn13;
    private Integer pagecount;
    private String coverImage;
    private String publishDate;
    private Wordcount wordcount;

    public boolean isMissingInfo() {
        return title == null || author == null || description == null || isbn10 == null || pagecount == null;
    }
}
