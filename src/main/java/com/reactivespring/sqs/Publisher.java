package com.reactivespring.sqs;

import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class Publisher {
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
//    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask(Review review) {
        log.info("*****Sending Message to SQS: {} ", review);
        //queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload("Niraj").build());
        queueMessagingTemplate.convertAndSend(endpoint, review.toString());
    }
}
