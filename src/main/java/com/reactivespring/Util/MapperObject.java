package com.reactivespring.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.sqs.MessageClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperObject {

    @Autowired
    private ObjectMapper objectMapper;

    public MessageClass mapToMessageClass(String message) {
        try {

            return objectMapper.readValue(message, MessageClass.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
