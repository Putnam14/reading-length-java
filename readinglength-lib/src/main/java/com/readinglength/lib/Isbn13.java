package com.readinglength.lib;

import org.springframework.util.StringUtils;

public class Isbn13 extends Isbn {
    private String isbn;

    public Isbn13(String isbn) {
        String temp = cleanIsbnString(isbn);
        boolean isValid = validate(temp);
        if (isValid) {
            this.isbn = temp;
        } else {
            throw new IllegalArgumentException(String.format(INVALID_ISBN, this.getClass(), isbn));
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public static Isbn13 convert(Isbn10 isbn10) {
        StringBuilder isbnBuilder = new StringBuilder(isbn10.getIsbn().substring(0,9));
        isbnBuilder.insert(0, "978");
        isbnBuilder.append(calcCheckSum(isbnBuilder.toString()));
        return new Isbn13(isbnBuilder.toString());
    }

    public static boolean validate(String isbn) {
        if (!StringUtils.hasLength(isbn)) return false;
        String temp = isbn.length() == 13 ? isbn : cleanIsbnString(isbn);
        if(temp.length() != 13) return false;

        try {
            int checkSum = calcCheckSum(temp);
            return checkSum == Integer.parseInt(temp.substring(12));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int calcCheckSum(String isbn) {
        int total = 0;

        for(int i = 0; i < 12; i++) {
            int digit = Integer.parseInt(isbn.substring(i, i + 1));
            total += (i & 1) == 0 ? digit : digit * 3;
        }

        total = 10 - (total % 10);

        return total == 10 ? 0 : total;
    }

}