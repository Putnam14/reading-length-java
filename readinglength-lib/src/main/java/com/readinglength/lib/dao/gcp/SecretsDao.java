package com.readinglength.lib.dao.gcp;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

import java.io.IOException;


public class SecretsDao {

    public static String getSecret(String secretId) throws Exception {
        String projectId = "reading-length";
        return quickstart(projectId, secretId);
    }

    private static String quickstart(String projectId, String secretId) throws IOException {

        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {

            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");

            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);

            client.close();

            return response.getPayload().getData().toStringUtf8();

        }
    }

}
