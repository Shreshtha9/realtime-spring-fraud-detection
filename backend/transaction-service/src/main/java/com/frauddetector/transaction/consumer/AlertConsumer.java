package com.frauddetector.transaction.consumer;

import com.frauddetector.transaction.model.AlertView;
import com.frauddetector.transaction.stream.AlertStreamHub;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertConsumer {

    private final AlertStreamHub hub;

    public AlertConsumer(AlertStreamHub hub) {
        this.hub = hub;
    }

    @KafkaListener(topics = "fraud-alerts")
    public void onAlert(AlertView alert) {
        hub.publish(alert);
    }
}