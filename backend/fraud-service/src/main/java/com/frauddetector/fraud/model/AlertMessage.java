package com.frauddetector.fraud.model;

import java.time.Instant;
import java.util.List;

public record AlertMessage(
        String transactionId,
        String userId,
        int riskScore,
        String decision,
        List<String> reasons,
        Instant createdAt) {

    public static AlertMessage from(FraudDecision d) {
        List<String> reasons = d.firedRules().stream()
                .map(r -> r.ruleName() + ": " + r.reason())
                .toList();
        return new AlertMessage(
                d.transactionId(), d.userId(), d.riskScore(),
                d.decision().name(), reasons, Instant.now());
    }
}