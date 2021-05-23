package com.readinglength.lib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class Isbn10Test {

    @ParameterizedTest
    @MethodSource("isbnProvider")
    void validate(String isbn, boolean expected) {
        assertEquals(expected, Isbn10.validate(isbn));
    }

    private static Stream<Arguments> isbnProvider() {
        return Stream.of(
                arguments(null, false),
                arguments("", false),
                arguments("abc", false),
                arguments("abcdefghij", false),
                arguments("          ", false),
                arguments("1234567890", false),
                arguments("123456789X", true),
                arguments("123456789X ", true),
                arguments("1-23-456789-X", true),
                arguments("X234567891", false),
                arguments("0132350882", true),
                arguments("0-13-235088-2", true),
                arguments("0-13-235088-2", true),
                arguments("013235088-1 ", false),
                arguments("9780679732761", false)
        );
    }

    @Test
    void testISBN10Valid() {
        String input = "0132350882";

        Isbn10 isbn = new Isbn10(input);

        assertEquals(input, isbn.toString());
    }

    @Test
    void testISBN10Invalid() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn10("1234567890"));
    }

    @Test
    void testConvert() {
        Isbn10 isbn = Isbn10.convert(new Isbn13("9780679732761"));
        assertTrue(Isbn10.validate(isbn.toString()));
    }
}