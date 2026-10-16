package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class VelocityRule implements FraudRule {

    private static final String NAME = "VELOCITY";
    private static final int WINDOW_SECONDS = 60;
    private static final int MAX_PAYMENTS = 5;

    private final JdbcTemplate jdbc;

    public VelocityRule(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "More than 5 payments by the same user within 60 seconds";
    }

    @Override
    public int score() {
        return 40;
    }

    @Override
    public RuleResult evaluate(TransactionEvent event) {
        Instant to = event.eventTime();
        Instant from = to.minusSeconds(WINDOW_SECONDS);

        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM transactions
                WHERE user_id = ? AND event_time > ? AND event_time <= ?
                """,
                Integer.class, event.userId(), Timestamp.from(from), Timestamp.from(to));

        if (count != null && count > MAX_PAYMENTS) {
            return RuleResult.flagged(NAME, 40,
                    count + " payments in " + WINDOW_SECONDS + " seconds (limit " + MAX_PAYMENTS + ")");
        }
        return RuleResult.clean(NAME);
    }
}