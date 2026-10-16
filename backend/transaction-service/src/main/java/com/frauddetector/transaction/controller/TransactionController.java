package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.model.TransactionEvent;
import com.frauddetector.transaction.model.TransactionRow;
import com.frauddetector.transaction.producer.TransactionPublisher;
import com.frauddetector.transaction.repository.TransactionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public record ManualPayment(String userId, double amount, String city, String deviceId) {
    }

    private final TransactionPublisher publisher;
    private final TransactionRepository transactions;

    public TransactionController(TransactionPublisher publisher, TransactionRepository transactions) {
        this.publisher = publisher;
        this.transactions = transactions;
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

    @GetMapping
    public List<TransactionRow> recent(@RequestParam(name = "limit", defaultValue = "50") int limit) {
        return transactions.findRecent(Math.min(limit, 200));
    }
}