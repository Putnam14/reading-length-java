package com.readinglength.researcherws.dao.amazon;

import com.readinglength.lib.dao.gcp.SecretsDao;
import org.junit.jupiter.api.Test;


class AmazonDaoTest {

    //@Test
    void test() throws Exception {
        String accessKey = SecretsDao.getSecret("AMAZON_API_KEY");
        String privateKey = SecretsDao.getSecret("AMAZON_API_SECRET");
        AmazonDao instance = new AmazonDao(accessKey, privateKey);
        instance.searchItems("the color of law");
    }
}