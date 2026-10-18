package com.frauddetector.fraud.model;

import java.time.Instant;
import java.util.List;

/** What the API returns for one decision: the score, the outcome and every rule that fired. */
public record DecisionView(
        String transactionId,
        String userId,
        int riskScore,
        String decision,
        List<RuleHit> rules,
        Instant decidedAt) {

    public record RuleHit(String name, int score, String reason) {
    }

    public static DecisionView from(FraudDecision d) {
        List<RuleHit> hits = d.firedRules().stream()
                .map(r -> new RuleHit(r.ruleName(), r.score(), r.reason()))
                .toList();
        return new DecisionView(d.transactionId(), d.userId(), d.riskScore(),
                d.decision().name(), hits, Instant.now());
    }
}