package com.readinglength.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsbnTest {

    @Test
    void ofIsbn10() {
        assertEquals(new Isbn10("0132350882").getIsbn(), Isbn.of("0-13-235088-2").getIsbn());
    }

    @Test
    void ofIsbn13() {
        assertEquals(new Isbn13("9789911457714").getIsbn(), Isbn.of("978-9911457714").getIsbn());
    }

    @Test
    void ofInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Isbn.of("123"));
        assertThrows(IllegalArgumentException.class, () -> Isbn.of(null));
    }

    @Test
    void convert() {
        assertThrows(IllegalStateException.class, () -> Isbn.convert(Isbn.of("0132350882")));
    }

    @Test
    void validate() {
        assertThrows(IllegalStateException.class, () -> Isbn.validate("0132350882"));
    }

    @Test
    void cleanIsbnString() {
        assertEquals("", Isbn.cleanIsbnString("  - "));
        assertEquals("1", Isbn.cleanIsbnString("  1- "));
        assertEquals("0132350882", Isbn.cleanIsbnString("0-13-235088-2 "));
    }

}