package com.readinglength.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @Test
    void testBuilder() {
        Book bookOne = new Book.Builder()
                .withIsbn13(new Isbn13("978-1-68051-004-1"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withPublisher("Mountaineers Books")
                .withPublishDate("2017-10-05")
                .withPagecount(624)
                .build();

        Book bookTwo = new Book.Builder()
                .withIsbn10(new Isbn10("1680510045"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withPublisher("Mountaineers Books")
                .withPublishDate(LocalDate.of(2017, 10, 5))
                .withPagecount(624)
                .build();

        assertTrue(bookOne.equals(bookTwo));
    }

    @Test
    void merge() {
        Book bookOne = new Book.Builder()
                .withIsbn13(new Isbn13("978-1-68051-004-1"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withPublisher("Mountaineers Books")
                .withPublishDate("2017-10-05")
                .withPagecount(624)
                .build();

        Book bookTwo = new Book.Builder()
                .withTitle("Mountaineeeeeeering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withDescription("The definitive guide to mountains and climbing . . .")
                .withPagecount(10)
                .build();

        bookOne.merge(bookTwo);

        Book expected = new Book.Builder()
                .withIsbn13(new Isbn13("978-1-68051-004-1"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withDescription("The definitive guide to mountains and climbing . . .")
                .withPublisher("Mountaineers Books")
                .withPublishDate("2017-10-05")
                .withPagecount(624)
                .build();

        assertEquals(expected, bookOne);
    }

    @Test
    void serialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Book start = new Book.Builder()
                .withIsbn13(new Isbn13("978-1-68051-004-1"))
                .withTitle("Mountaineering: The Freedom of the Hills, 9th Edition")
                .withAuthor("The Mountaineers")
                .withPublisher("Mountaineers Books")
                .withPublishDate("2017-10-05")
                .withPagecount(624)
                .build();

        String serialized = objectMapper.writeValueAsString(start);

        Book end = objectMapper.readValue(serialized, Book.class);

        assertEquals(start, end);
    }
}