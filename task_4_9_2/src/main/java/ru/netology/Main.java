package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private static final String REMOTE_SERVICE_URI =
            "https://api.nasa.gov/planetary/apod?api_key";
    private static final String API_KEY =
            "ep7jo0QQhPs5h18ewCEeE4JfHXWvCSndrPhJpLVk";

    private static final String imageFullFileName = "C://Users//Andrew//IdeaProjects//task_4_9_2//" +
            "src//main//resources//image_01.jpg";

    private static final String FULL_REMOTE_SERVICE_URI = REMOTE_SERVICE_URI + "=" + API_KEY;

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(FULL_REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        Post post = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        String mediaType = post.getMediaType();
        if (!mediaType.equals("image")) {
            System.out.println("Cannot process media of type " + mediaType + ".");
        } else {
            String imageUri = post.getHdUrl();
            System.out.println("HD URL is " + imageUri);

            request = new HttpGet(imageUri);
            response = httpClient.execute(request);

            FileOutputStream fileOutputStream = new FileOutputStream(imageFullFileName);
            fileOutputStream.write(response.getEntity().getContent().readAllBytes());
            fileOutputStream.close();

            System.out.println("Image have been successfully saved.");
        }

        response.close();
        httpClient.close();
    }
}