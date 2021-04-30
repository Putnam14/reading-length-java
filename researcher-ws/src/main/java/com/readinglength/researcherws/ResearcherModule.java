package com.readinglength.researcherws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readinglength.lib.dao.gcp.SecretsDao;
import com.readinglength.lib.ws.RestClient;
import com.readinglength.researcherws.dao.amazon.AmazonDao;
import com.readinglength.researcherws.dao.google.GoogleBooksDao;
import com.readinglength.researcherws.dao.openlibrary.OpenLibraryDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.net.URL;

@Module
class ResearcherModule {
    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    AmazonDao providesAmazonDao() {
        String accessKey = null;
        String secretKey = null;
        try {
            accessKey = SecretsDao.getSecret("AMAZON_API_KEY");
            secretKey = SecretsDao.getSecret("AMAZON_API_SECRET");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new AmazonDao(accessKey, secretKey);
    }

    @Provides
    @Singleton
    GoogleBooksDao providesGoogleBooksDao() {
        URL baseUrl = null;
        String key = null;
        try {
            baseUrl = new URL("https://www.googleapis.com/books/v1/");
            key = SecretsDao.getSecret("GOOGLE_BOOKS_API_KEY");
        } catch (Exception e) {
           e.printStackTrace();
        }
        return new GoogleBooksDao(new RestClient(), baseUrl, key);
    }

    @Provides
    @Singleton
    OpenLibraryDao providesOpenLibraryDao() {
        return new OpenLibraryDao(new RestClient());
    }
}
