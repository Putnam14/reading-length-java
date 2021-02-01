package com.readinglength.lib;

import org.springframework.util.StringUtils;

public class Isbn10 extends Isbn {
    private String isbn;

    Isbn10(String isbn) {
        boolean isValid = validate(isbn);
        if (isValid) {
            this.isbn = isbn;
        } else {
            throw new IllegalArgumentException(String.format("Provided Isbn is invalid for %s: %s", this.getClass().toGenericString(), isbn));
        }
    }

    public static Isbn10 convert(Isbn13 isbn13) {
        StringBuilder isbnBuilder = new StringBuilder(isbn13.getIsbn().substring(3,12));
        isbnBuilder.append(calcCheckSum(isbnBuilder.toString()));
        return new Isbn10(isbnBuilder.toString());
    }

    public String getIsbn() {
        return isbn;
    }

    public static boolean validate(String isbn) {
        if (!StringUtils.hasLength(isbn)) return false;

        String temp = isbn
                .trim()
                .replaceAll("-","");

        if (temp.length() != 10) return false;

        try {
            String checkSum = calcCheckSum(temp);
            return checkSum.equals(temp.substring(9));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String calcCheckSum(String isbn) {
        int total = 0;
        for(int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(isbn.substring(i, i + 1));
            total += (10 - i) * digit;
        }
        total = (11 - (total % 11)) % 11;
        String checkSum = Integer.toString(total);
        if("10".equals(checkSum)) checkSum = "X";
        return checkSum;
    }

}
