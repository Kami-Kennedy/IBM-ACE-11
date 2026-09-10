package com.ubagroup.sochitel;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ParseMsisdn {

    private static final String API_URL  = "https://artx.sochitel.com/api.php";
    private static final String USERNAME = "ubamalixof";
    private static final String PASSWORD = "e?TMV@*9ybs}e'?";

  
    public static String parseMsisdn(String msisdn) {
        try {
            String passwordHash = com.ubagroup.sha1.Sha1Encryption.getSecurePassword(PASSWORD);
            String salt        = passwordHash.substring(passwordHash.indexOf('.') + 1);
            String pass         = passwordHash.substring(0, passwordHash.indexOf('.'));

            String reqBody =
                "{\"auth\":{" +
                    "\"username\":\"" + USERNAME + "\"," +
                    "\"salt\":\""     + salt     + "\"," +
                    "\"password\":\"" + pass     + "\"," +
                    "\"signature\":\"\"" +
                "}," +
                "\"command\":\"parseMsisdn\"," +
                "\"version\":\"5\"," +
                "\"msisdn\":\""  + msisdn + "\"}";

            // Return both request and response so ESQL can log both
            String rawResponse = postToSochitel(reqBody);

            return "||REQ||" + reqBody + "||RES||" + rawResponse;

        } catch (Exception e) {
            return "||REQ||BUILD_FAILED||RES||{\"status\":{\"id\":-1,\"name\":\"" 
                   + e.getMessage() + "\"}}";
        }
    }

 
    private static String postToSochitel(String body) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute(post);
            try {
                return EntityUtils.toString(
                    response.getEntity(), StandardCharsets.UTF_8);
            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }


    private static String sha512(String input) throws Exception {
        MessageDigest md   = MessageDigest.getInstance("SHA-512");
        byte[]        hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb   = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

   
    private static String generateSalt() {
        byte[]        bytes = new byte[20];
        new Random().nextBytes(bytes);
        StringBuilder sb    = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}