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

class Isbn13Test {

    @ParameterizedTest
    @MethodSource("isbnProvider")
    void validate(String isbn, boolean expected) {
        assertEquals(expected, Isbn13.validate(isbn));
    }

    static Stream<Arguments> isbnProvider() {
        return Stream.of(
                arguments(null, false),
                arguments("", false),
                arguments("abc", false),
                arguments("abcdefghijklm", false),
                arguments("             ", false),
                arguments("1234567890000", false),
                arguments("123456789X", false),
                arguments("0-13-235088-2", false),
                arguments("978-0-679-73276-1", true),
                arguments("9780679732761", true)
        );
    }

    @Test
    void testISBN13_valid() {
        String input = "9780679732761";

        Isbn13 isbn = new Isbn13(input);

        assertEquals(input, isbn.getIsbn());
    }

    @Test
    void testISBN10_invalid() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn13("9780679732762"));
    }

    @Test
    void testConvert() {
        Isbn13 isbn = Isbn13.convert(new Isbn10("123456789X"));
        assertTrue(Isbn13.validate(isbn.getIsbn()));
    }

}

