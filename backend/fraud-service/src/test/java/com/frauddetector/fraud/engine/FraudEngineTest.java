package com.frauddetector.fraud.engine;

import com.frauddetector.fraud.model.Decision;
import com.frauddetector.fraud.model.FraudDecision;
import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.rules.FraudRule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pure unit tests: no Spring, no database, no Kafka. */
class FraudEngineTest {

    private static final TransactionEvent EVENT =
            new TransactionEvent("t1", "U1", 500, "Pune", "device-1-a", Instant.now());

    /** A fake rule that either fires with the given score or stays quiet. */
    private static FraudRule rule(String name, int score, boolean fires) {
        return new FraudRule() {
            public RuleResult evaluate(TransactionEvent e) {
                return fires ? RuleResult.flagged(name, score, "test") : RuleResult.clean(name);
            }
            public String name() { return name; }
            public String description() { return name; }
            public int score() { return score; }
        };
    }

    private static FraudDecision run(FraudRule... rules) {
        return new FraudEngine(List.of(rules)).evaluate(EVENT);
    }

    @Test
    void noRulesFired_isAllow() {
        FraudDecision d = run(rule("A", 40, false), rule("B", 30, false));
        assertEquals(0, d.riskScore());
        assertEquals(Decision.ALLOW, d.decision());
    }

    @Test
    void justBelowReviewThreshold_isAllow() {
        assertEquals(Decision.ALLOW, run(rule("A", 29, true)).decision());
    }

    @Test
    void atReviewThreshold_isReview() {
        assertEquals(Decision.REVIEW, run(rule("A", 30, true)).decision());
    }

    @Test
    void justBelowBlockThreshold_isReview() {
        assertEquals(Decision.REVIEW, run(rule("A", 69, true)).decision());
    }

    @Test
    void atBlockThreshold_isBlock() {
        assertEquals(Decision.BLOCK, run(rule("A", 70, true)).decision());
    }

    @Test
    void scoresAreSummedAndCappedAt100() {
        FraudDecision d = run(rule("A", 40, true), rule("B", 40, true), rule("C", 40, true));
        assertEquals(100, d.riskScore());
        assertEquals(Decision.BLOCK, d.decision());
        assertEquals(3, d.firedRules().size());
    }
}