package com.reactivespring.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Component
public class S3Helper {

    private ObjectMapper objectMapper;

    private AmazonS3 amazonS3Client;

    private String s3BucketName;

    public S3Helper(ObjectMapper objectMapper, AmazonS3 amazonS3Client, @Value("${s3.aws.bucket.name}") String s3BucketName) {
        this.objectMapper = objectMapper;
        this.amazonS3Client = amazonS3Client;
        this.s3BucketName = s3BucketName;
    }


    public Review getFileFromS3(String key, String versionId) {

        final S3Object object = amazonS3Client.getObject(new GetObjectRequest(s3BucketName, key, versionId));
        final String file = getAsString(object.getObjectContent());
        try {
           return objectMapper.readValue(file, Review.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private String getAsString(S3ObjectInputStream objectContent) {
        if (objectContent == null) {
            return " ";
        }
        StringBuilder sb = new StringBuilder();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(objectContent, StringUtils.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
