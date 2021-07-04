package com.readinglength.lib;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Wordcount.Builder.class)
public class Wordcount {
    private Isbn13 isbn;
    private int words;
    private Integer userId;
    private WordcountType type;

    private Wordcount(Isbn13 isbn, int words, Integer userId, WordcountType type) {
        this.isbn = isbn;
        this.words = words;
        this.userId = userId;
        this.type = type;
    }

    public Isbn getIsbn() {
        return isbn;
    }

    @JsonGetter("isbn")
    public String getIsbnString() {
        return isbn.toString();
    }

    public int getWords() {
        return words;
    }

    public WordcountType getType() {
        return type;
    }

    @JsonGetter("type")
    public int getTypeId() {
        return type.getId();
    }

    public Integer getUserId() {
        return userId;
    }


    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private Isbn13 isbn;
        private int userId;
        private int words;
        private int type;

        public Builder() {}
        public Builder(Wordcount wordcount) {
            this.isbn = wordcount.isbn;
            this.userId = wordcount.userId;
            this.words = wordcount.words;
            this.type = wordcount.getTypeId();
        }

        public Builder withIsbn(Isbn13 isbn)    { this.isbn = isbn; return this; }
        public Builder withUserId(int userId)   { this.userId = userId; return this; }
        public Builder withWords(int words)     { this.words = words; return this; }
        public Builder withType(int type)       { this.type = type; return this; }

        public Wordcount build() { return new Wordcount(isbn, words, userId, WordcountType.byId(type)); }
    }

    public enum WordcountType {

        UNKNOWN(-1, "Unknown"),
        GUESS(0, "Guess"),
        ESTIMATE(1, "Estimate"),
        AUDIOBOOK(2, "Audiobook length"),
        CALCULATED(3, "Calculated"),
        USER(4, "User submitted"),
        AUTHOR(5, "Author verified");



        private int id;
        private String description;

        WordcountType(int id, String description) {
            this.id = id;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public static WordcountType byId(int id) {
            for (WordcountType t : values()) {
                if (t.id == id) return t;
            }
            return UNKNOWN;
        }
    }
}
