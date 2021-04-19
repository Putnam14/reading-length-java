package com.readinglength.researcherws.dao.amazon;

import com.readinglength.lib.Book;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AmazonServiceTest {
    AmazonDao mockDao = mock(AmazonDao.class);
    AmazonService instance = new AmazonService(mockDao);

    @Test
    void searchKeyword() {
        when(mockDao.searchItems("the color of law")).thenReturn(loadJson("json/amazonDaoResponse-colorOfLaw.json"));
        Book result = instance.searchKeyword("the color of law");
        assertAll(
                () -> assertEquals("The Color of Law: A Forgotten History of How Our Government Segregated America", result.getTitle()),
                () -> assertEquals("Rothstein, Richard", result.getAuthor()),
                () -> assertEquals("1631494538", result.getIsbn10().toString()),
                () -> assertEquals("9781631494536", result.getIsbn13().toString()),
                () -> assertEquals(368, result.getPagecount()),
                () -> assertEquals("Liveright Publishing Corporation", result.getPublisher()),
                () -> assertEquals("2018-04-30", result.getPublishDate())
        );
    }

    private static String loadJson(String path) {
        try {
            return Files.readString(Path.of(
                    AmazonServiceTest.class.getClassLoader().getResource(path).getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}