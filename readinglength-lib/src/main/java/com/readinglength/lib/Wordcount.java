package com.readinglength.lib;

public class Wordcount {
    private Isbn isbn;
    private int words;
    private Integer userId;
    private WordcountType type;

    public Wordcount(Isbn isbn, int words, Integer userId, WordcountType type) {
        this.isbn = isbn;
        this.words = words;
        this.userId = userId;
        this.type = type;
    }

    public Isbn getIsbn() {
        return isbn;
    }

    public int getWords() {
        return words;
    }

    public WordcountType getType() {
        return type;
    }

    public Integer getUserId() {
        return userId;
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
