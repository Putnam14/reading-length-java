package com.readinglength.libraryws.auth;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import java.io.IOException;

@Singleton
public class Authentication {
    private static Logger LOG = LoggerFactory.getLogger(Authentication.class);
    private IdTokenCredentials.Builder credentialsBuilder;

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
    public HttpResponse makeGetRequest(String serviceUrl) throws IOException {
        IdTokenCredentials tokenCredentials = credentialsBuilder
                .setTargetAudience(serviceUrl)
                .build();

        GenericUrl genericUrl = new GenericUrl(serviceUrl);
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(tokenCredentials);
        HttpTransport transport = new NetHttpTransport();
        HttpRequest request = transport.createRequestFactory(adapter).buildGetRequest(genericUrl);
        return request.execute();
    }
}