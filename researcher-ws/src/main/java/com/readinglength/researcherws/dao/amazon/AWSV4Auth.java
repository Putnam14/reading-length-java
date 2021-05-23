package com.readinglength.researcherws.dao.amazon;

/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class AWSV4Auth {
    private static final String HMAC_ALGORITHM = "AWS4-HMAC-SHA256";
    private static final String AWS_4_REQUEST = "aws4_request";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private String awsAccessKey;
    private String awsSecretKey;
    private String path;
    private String region;
    private String service;
    private String httpMethodName;
    private TreeMap<String, String> headers;
    private String payload;
    private String signedHeaders;
    private String xAmzDate;
    private String currentDate;

    private AWSV4Auth(Builder builder) {
        awsAccessKey = builder.awsAccessKey;
        awsSecretKey = builder.awsSecretKey;
        path = builder.path;
        region = builder.region;
        service = builder.service;
        httpMethodName = builder.httpMethodName;
        headers = builder.headers;
        payload = builder.payload;
        xAmzDate = getTimeStamp();
        currentDate = getDate();
    }

    Map<String, String> getHeaders() {
        headers.put("x-amz-date", xAmzDate);

        // Step 1: CREATE A CANONICAL REQUEST
        String canonicalURL = prepareCanonicalRequest();

        // Step 2: CREATE THE STRING TO SIGN
        String stringToSign = prepareStringToSign(canonicalURL);

        // Step 3: CALCULATE THE SIGNATURE
        String signature = calculateSignature(stringToSign);

        // Step 4: CALCULATE AUTHORIZATION HEADER
        if (signature != null) {
            headers.put("Authorization", buildAuthorizationString(signature));
            return headers;
        } else {
            return null;
        }
    }

    private String prepareCanonicalRequest() {
        StringBuilder canonicalUrl = new StringBuilder();

        canonicalUrl.append(httpMethodName).append("\n");

        canonicalUrl.append(path).append("\n").append("\n");

        StringBuilder signedHeaderBuilder = new StringBuilder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entrySet : headers.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                signedHeaderBuilder.append(key).append(";");
                canonicalUrl.append(key).append(":").append(value).append("\n");
            }
            canonicalUrl.append("\n");
        } else {
            canonicalUrl.append("\n");
        }

        signedHeaders = signedHeaderBuilder.substring(0, signedHeaderBuilder.length() - 1);
        canonicalUrl.append(signedHeaders).append("\n");

        payload = payload == null ? "" : payload;
        canonicalUrl.append(toHex(payload));

        return canonicalUrl.toString();
    }

    private String prepareStringToSign(String canonicalUrl) {
        return String.format("%s\n%s\n%s/%s/%s/%s\n%s",
                HMAC_ALGORITHM,
                xAmzDate,
                currentDate,
                region,
                service,
                AWS_4_REQUEST,
                toHex(canonicalUrl));
    }

    private String calculateSignature(String stringToSign) {
        try {
            byte[] signatureKey = getSignatureKey(awsSecretKey, currentDate, region, service);
            byte[] signature = hmacSha256(signatureKey, stringToSign);
            return bytesToHex(signature);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String buildAuthorizationString(String signature) {
        return String.format("%s Credential=%s/%s/%s/%s/%s,SignedHeaders=%s,Signature=%s",
                HMAC_ALGORITHM,
                awsAccessKey,
                getDate(),
                region,
                service,
                AWS_4_REQUEST,
                signedHeaders,
                signature);
    }

    static class Builder {
        private String awsAccessKey;
        private String awsSecretKey;
        private String path;
        private String region;
        private String service;
        private String httpMethodName;
        private TreeMap<String, String> headers;
        private String payload;

        Builder(String awsAccessKey, String awsSecretKey) {
            this.awsAccessKey = awsAccessKey;
            this.awsSecretKey = awsSecretKey;
        }

        Builder path(String path) {
            this.path = path;
            return this;
        }

        Builder region(String region) {
            this.region = region;
            return this;
        }

        Builder service(String service) {
            this.service = service;
            return this;
        }

        Builder httpMethodName(String httpMethodName) {
            this.httpMethodName = httpMethodName;
            return this;
        }

        Builder headers(TreeMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        AWSV4Auth build() {
            return new AWSV4Auth(this);
        }
    }

    private static String toHex(String data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] hmacSha256(byte[] key, String data) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] getSignatureKey(String key, String date, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSha256(kSecret, date);
        byte[] kRegion = hmacSha256(kDate, regionName);
        byte[] kService = hmacSha256(kRegion, serviceName);
        return hmacSha256(kService, AWS_4_REQUEST);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    private static String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }
}
