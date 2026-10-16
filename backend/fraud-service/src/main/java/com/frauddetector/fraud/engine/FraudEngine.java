package com.frauddetector.fraud.engine;

import com.frauddetector.fraud.model.Decision;
import com.frauddetector.fraud.model.FraudDecision;
import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.rules.FraudRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FraudEngine {

    private final List<FraudRule> rules;

    public FraudEngine(List<FraudRule> rules) {
        this.rules = rules;
    }

    public FraudDecision evaluate(TransactionEvent event) {
        List<RuleResult> fired = rules.stream()
                .map(rule -> rule.evaluate(event))
                .filter(RuleResult::triggered)
                .toList();

        int total = fired.stream().mapToInt(RuleResult::score).sum();
        int riskScore = Math.min(total, 100);          // score 100 se upar nahi jaayega

        return new FraudDecision(
                event.transactionId(), event.userId(),
                riskScore, decide(riskScore), fired);
    }

    private Decision decide(int riskScore) {
        if (riskScore >= 70) return Decision.BLOCK;
        if (riskScore >= 30) return Decision.REVIEW;
        return Decision.ALLOW;
    }
}