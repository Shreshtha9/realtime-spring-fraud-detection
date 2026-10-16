package com.frauddetector.transaction.model;

import java.time.Instant;

public record TransactionRow(
        String transactionId,
        String userId,
        double amount,
        String city,
        String deviceId,
        Instant eventTime) {
}