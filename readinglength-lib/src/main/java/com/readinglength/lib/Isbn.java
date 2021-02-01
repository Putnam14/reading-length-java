package com.readinglength.lib;

public abstract class Isbn {
    public abstract String getIsbn();

    public static Isbn of(String isbn) {
        if (Isbn10.validate(isbn)) {
            return new Isbn10(isbn);
        } else if (Isbn13.validate(isbn)) {
            return new Isbn13(isbn);
        }
        throw new IllegalArgumentException("ISBN not valid.");
    }
}
