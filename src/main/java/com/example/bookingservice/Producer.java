package com.example.bookingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer
{
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "payment-events";

    @Autowired //DEPENDENCY INJECTION PROMISE FULFILLED AT RUNTIME
    private KafkaTemplate<String, String> kafkaTemplate ;

    public void pubDomainEvent(PaymentEvent paymentEvent) throws JsonProcessingException // LOGIN | REGISTER
    {
        // convert to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String datum = objectMapper.writeValueAsString(paymentEvent);

        logger.info(String.format("#### -> Producing message -> %s", datum));
        this.kafkaTemplate.send(TOPIC,"1", datum);
    }
}
