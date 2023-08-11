package com.reactivespring.sns;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SNSPublisher {

    @Value("${sns.topic.arn}")
    private String topicArn;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmazonSNSClient amazonSNSClient;

    public void sendMessageToSQS(Review review){

        PublishRequest publishRequest = null;
        try {
            publishRequest = new PublishRequest(topicArn, objectMapper.writeValueAsString(review));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var result = amazonSNSClient.publish(publishRequest);
        log.info("****Message sent to SNS Topic: {}", result.getMessageId());

    }

}
