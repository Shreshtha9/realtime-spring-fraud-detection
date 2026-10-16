package com.frauddetector.transaction.model;

import java.time.Instant;
import java.util.List;

public record AlertView(
        String transactionId,
        String userId,
        int riskScore,
        String decision,
        List<String> reasons,
        Instant createdAt) {
}