package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.model.TransactionEvent;
import com.frauddetector.transaction.producer.TransactionPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public record ManualPayment(String userId, double amount, String city, String deviceId) {
    }

    private final TransactionPublisher publisher;

    public TransactionController(TransactionPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public Map<String, String> create(@RequestBody ManualPayment p) {
        TransactionEvent event = new TransactionEvent(
                UUID.randomUUID().toString(),
                p.userId(), p.amount(), p.city(), p.deviceId(),
                Instant.now());
        publisher.publish(event);
        return Map.of("transactionId", event.transactionId());
    }
}