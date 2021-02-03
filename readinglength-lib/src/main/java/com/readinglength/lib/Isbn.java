package com.readinglength.lib;

public abstract class Isbn {
    static String INVALID_ISBN = "Provided Isbn is invalid for %s: %s"; // NOPMD

    public abstract String getIsbn();

    public static Isbn of(String isbn) {
        if (Isbn10.validate(isbn)) {
            return new Isbn10(isbn);
        } else if (Isbn13.validate(isbn)) {
            return new Isbn13(isbn);
        }
        throw new IllegalArgumentException("ISBN not valid.");
    }

    public static Isbn convert(Isbn isbn) {
        throw new IllegalStateException("Convert has not been set up.");
    }

    public static boolean validate(String isbn) {
        throw new IllegalStateException("Validate has not been set up.");
    }


    static String cleanIsbnString(String isbn) { // NOPMD
        if (isbn == null) throw new IllegalArgumentException("Null ISBN entered.");
        return isbn
                .trim()
                .replaceAll("-", "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Isbn) {
            Isbn anIsbn = (Isbn) obj;
            return this.getIsbn().equals(anIsbn.getIsbn());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getIsbn().hashCode();
    }
}
