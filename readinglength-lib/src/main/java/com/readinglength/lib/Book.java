package com.readinglength.lib;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.github.sisyphsu.dateparser.DateParserUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonDeserialize(builder = Book.Builder.class)
public class Book {
    private String title;
    private String author;
    private String description;
    private Isbn10 isbn10;
    private Isbn13 isbn13;
    private Integer pagecount;
    private String coverImage;
    private String publisher;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;
    private Wordcount wordcount;

    private Book(String title, String author, String description, Isbn10 isbn10, Isbn13 isbn13, Integer pagecount, String coverImage, String publisher, LocalDate publishDate, Wordcount wordcount) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.pagecount = pagecount;
        this.coverImage = coverImage;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.wordcount = wordcount;
    }

    public void merge(Book toMerge) {
        if (title == null) title = toMerge.title;
        if (author == null) author = toMerge.author;
        if (description == null) description = toMerge.description;
        if (isbn10 == null && toMerge.isbn10 != null) { isbn10 = toMerge.isbn10; isbn13 = Isbn13.convert(toMerge.isbn10); }
        if (pagecount == null) pagecount = toMerge.pagecount;
        if (coverImage == null) coverImage = toMerge.coverImage;
        if (publisher == null) publisher = toMerge.publisher;
        if (publishDate == null) publishDate = toMerge.publishDate;
        if (wordcount == null) wordcount = toMerge.wordcount;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public Isbn10 getIsbn10() {
        return isbn10;
    }

    public Isbn13 getIsbn13() {
        return isbn13;
    }

    public Integer getPagecount() {
        return pagecount;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public Wordcount getWordcount() {
        return wordcount;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
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

        public Builder() {}
        public Builder(Book book) {
            this.title =        book.title;
            this.author =       book.author;
            this.description =  book.description;
            this.isbn10 =       book.isbn10;
            this.isbn13 =       book.isbn13;
            this.pagecount =    book.pagecount;
            this.coverImage =   book.coverImage;
            this.publisher =    book.publisher;
            this.publishDate =  book.publishDate;
            this.wordcount =    book.wordcount;
        }

        public Builder withTitle(String title)                  { this.title = title; return this; }
        public Builder withAuthor(String author)                { this.author = author; return this; }
        public Builder withDescription(String description)      { this.description = description; return this; }
        public Builder withIsbn10(Isbn10 isbn10)                { this.isbn10 = isbn10; this.isbn13 = Isbn13.convert(isbn10); return this; }
        public Builder withIsbn13(Isbn13 isbn13)                { this.isbn13 = isbn13; this.isbn10 = Isbn10.convert(isbn13); return this; }
        public Builder withPagecount(Integer pagecount)         { this.pagecount = pagecount; return this; }
        public Builder withCoverImage(String coverImage)        { this.coverImage = coverImage; return this; }
        public Builder withPublisher(String publisher)          { this.publisher = publisher; return this; }
        public Builder withPublishDate(LocalDate publishDate)   { this.publishDate = publishDate; return this; }
        public Builder withPublishDate(String publishDate)      { this.publishDate = stringToDateHelper(publishDate); return this; }
        public Builder withWordcount(Wordcount wordcount)       { this.wordcount = wordcount; return this; }

        public Book build() { return new Book(title, author, description, isbn10, isbn13, pagecount, coverImage, publisher, publishDate, wordcount); }
    }

    private static LocalDate stringToDateHelper(String dateString) {
        if (dateString != null) {
            try {
                return DateParserUtils.parseDateTime(dateString).toLocalDate();
            } catch (DateTimeParseException e) {
                Matcher m = Pattern.compile("(\\d{4})").matcher(dateString);
                if (m.find()) {
                    return DateParserUtils.parseDateTime(m.group(0)).toLocalDate();
                } else {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return isbn13.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title)
                && Objects.equals(author, book.author)
                && Objects.equals(description, book.description)
                && Objects.equals(isbn10, book.isbn10)
                && Objects.equals(isbn13, book.isbn13)
                && Objects.equals(pagecount, book.pagecount)
                && Objects.equals(publisher, book.publisher)
                && Objects.equals( publishDate, book.publishDate);
    }
}
