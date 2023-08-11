package com.reactivespring.sqs;

import com.reactivespring.S3.S3Mapper;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class S3SQSListener {

    @Autowired
    private S3Mapper s3Mapper;

    @SqsListener(value = "s3-to-sqs-demo", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(String message) {
        log.info("****S3 message received from SQS: {}", message);
        final Review review = s3Mapper.getAndReadFile(message);
        log.info("**** Extracted review data from S3 file successfully: {}", review);

    }
}
