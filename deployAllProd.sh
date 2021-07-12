#!/bin/sh

mvn clean install

cd ./researcher-ws
mvn jib:build
gcloud run deploy researcher-ws --image gcr.io/reading-length/researcher-ws:$1 --service-account researcher-identity --no-allow-unauthenticated

cd ../archivist-ws
mvn jib:build
gcloud run deploy archivist-ws --image gcr.io/reading-length/archivist-ws:$1 --service-account archivist-identity --no-allow-unauthenticated

cd ../library-ws
mvn jib:build
gcloud run deploy library-ws --image gcr.io/reading-length/library-ws:$1 --service-account library-identity --no-allow-unauthenticated