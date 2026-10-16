package com.frauddetector.fraud.producer;

import com.frauddetector.fraud.model.AlertMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertPublisher {

    private static final String TOPIC = "fraud-alerts";

    private final KafkaTemplate<String, AlertMessage> kafkaTemplate;

    public AlertPublisher(KafkaTemplate<String, AlertMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AlertMessage alert) {
        kafkaTemplate.send(TOPIC, alert.userId(), alert);
    }
}