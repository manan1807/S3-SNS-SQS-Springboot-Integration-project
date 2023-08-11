package com.reactivespring.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.Util.MapperObject;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.stereotype.Component;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;


@Component
@Slf4j
public class Listener {

    private MapperObject mapperObject;
    private ObjectMapper objectMapper;

    public Listener(MapperObject mapperObject, ObjectMapper objectMapper) {
        this.mapperObject = mapperObject;
        this.objectMapper = objectMapper;
    }


    @SqsListener(value = "DemoQueue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(String message) {

        try {
            var messageObj = mapperObject.mapToMessageClass(message);

            var review = objectMapper.readValue(messageObj.getMessage(), Review.class);
            log.info("****Message {}", review);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.info("****Message from SQS: {}", message);

    }
}
