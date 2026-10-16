package com.frauddetector.fraud.model;

import java.util.List;

public record FraudDecision(
        String transactionId,
        String userId,
        int riskScore,
        Decision decision,
        List<RuleResult> firedRules) {
}