package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnusualAmountRule implements FraudRule {

    private static final String NAME = "UNUSUAL_AMOUNT";
    private final JdbcTemplate jdbc;

    public UnusualAmountRule(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
      @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "Payment is more than 10 times the user's average amount.";
    }

    @Override
    public int score() {
        return 40;
    }

    @Override
    public RuleResult evaluate(TransactionEvent event) {
        List<Double> result = jdbc.queryForList(
                "SELECT avg_amount FROM users WHERE user_id = ?",
                Double.class, event.userId());

        if (result.isEmpty()) {
            return RuleResult.clean(NAME);      // user DB me nahi mila, to rule skip
        }

        double avg = result.get(0);
        if (event.amount() > avg * 10) {
            return RuleResult.flagged(NAME, score(),
                    "Amount " + event.amount() + " is more than 10x user's average " + avg);
        }
        return RuleResult.clean(NAME);
    }
}