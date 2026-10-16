package com.frauddetector.fraud.model;

import java.time.Instant;

public record TransactionEvent(
        String transactionId,
        String userId,
        double amount,
        String city,
        String deviceId,
        Instant eventTime) {
}