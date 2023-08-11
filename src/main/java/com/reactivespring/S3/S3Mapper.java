package com.reactivespring.S3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class S3Mapper {

    private ObjectMapper objectMapper;
    private S3Helper s3Helper;

    public S3Mapper(ObjectMapper objectMapper, S3Helper s3Helper) {
        this.objectMapper = objectMapper;
        this.s3Helper = s3Helper;
    }

    public Review getAndReadFile(String message) {

        try {
            var s3Event = objectMapper.readValue(message, S3Event.class);
            var s3Object = s3Event.getRecords().get(0).getS3().getObject();
            log.info("****Extracted S3Object from message metaData: {}", s3Object);
            return s3Helper.getFileFromS3(s3Object.getKey(), s3Object.getVersionId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
