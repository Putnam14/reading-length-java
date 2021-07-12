package com.readinglength.researcherws;

import com.readinglength.lib.dao.gcp.SecretsDao;
import com.readinglength.researcherws.dao.amazon.AmazonDao;
import com.readinglength.researcherws.dao.google.GoogleBooksDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
class ResearcherModule {

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
        String key = null;
        try {
            key = SecretsDao.getSecret("GOOGLE_BOOKS_API_KEY");
        } catch (Exception e) {
           e.printStackTrace();
        }
        return new GoogleBooksDao(key);
    }
}
