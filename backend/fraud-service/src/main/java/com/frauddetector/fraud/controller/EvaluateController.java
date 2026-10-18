package com.frauddetector.fraud.controller;

import com.frauddetector.fraud.model.DecisionView;
import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.processor.PaymentProcessor;
import com.frauddetector.fraud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Synchronous decision API: send a payment, get ALLOW / REVIEW / BLOCK back in the response. */
@RestController
@RequestMapping("/api/evaluate")
public class EvaluateController {

    public record EvaluateRequest(String transactionId, String userId, Double amount,
                                  String city, String deviceId, Instant eventTime) {
    }

    private final PaymentProcessor processor;
    private final UserRepository users;

    public EvaluateController(PaymentProcessor processor, UserRepository users) {
        this.processor = processor;
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<?> evaluate(@RequestBody EvaluateRequest r) {
        if (isBlank(r.userId()) || isBlank(r.city()) || isBlank(r.deviceId())
                || r.amount() == null || r.amount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "userId, city, deviceId and a positive amount are required"));
        }
        if (!users.exists(r.userId())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                    "error", "Unknown user: " + r.userId()));
        }

        TransactionEvent event = new TransactionEvent(
                isBlank(r.transactionId()) ? UUID.randomUUID().toString() : r.transactionId(),
                r.userId(), r.amount(), r.city(), r.deviceId(),
                r.eventTime() == null ? Instant.now() : r.eventTime());

        DecisionView decision = processor.process(event);
        return ResponseEntity.ok(decision);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}