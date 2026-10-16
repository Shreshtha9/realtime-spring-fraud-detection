package com.frauddetector.transaction.producer;

import com.frauddetector.transaction.model.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionPublisher {

    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionPublisher(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionEvent event) {
        kafkaTemplate.send(TOPIC, event.userId(), event);
    }
}