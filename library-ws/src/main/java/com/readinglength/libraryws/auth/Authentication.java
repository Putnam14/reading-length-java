package com.readinglength.libraryws.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.IOException;

@Singleton
public class Authentication {
    private static Logger LOG = LoggerFactory.getLogger(Authentication.class);
    private IdTokenCredentials.Builder credentialsBuilder;

    @Inject
    public Authentication() {
            try {
                GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                if (!(credentials instanceof IdTokenProvider)) {
                    throw new IllegalArgumentException("Credentials are not an instance of IdTokenProvider.");
                }
                credentialsBuilder = IdTokenCredentials.newBuilder()
                        .setIdTokenProvider((IdTokenProvider) credentials);
            } catch(IOException e) {
                LOG.error("Issue getting credentials", e);
                throw new IllegalStateException("Credentials are invalid", e);
            }
    }

    // makeGetRequest makes a GET request to the specified Cloud Run or
    // Cloud Functions endpoint, serviceUrl (must be a complete URL), by
    // authenticating with an Id token retrieved from Application Default Credentials.
    public String getToken(String serviceUrl) {
        IdTokenCredentials tokenCredentials = credentialsBuilder
                .setTargetAudience(serviceUrl)
                .build();

        try {
            return tokenCredentials.refreshAccessToken().getTokenValue();
        } catch(IOException e) {
            LOG.error("Error refreshing token", e);
            throw new IllegalStateException("Auth in bad state", e);
        }
    }
}