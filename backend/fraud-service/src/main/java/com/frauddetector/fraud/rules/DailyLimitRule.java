package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class DailyLimitRule implements FraudRule {

    private static final String NAME = "DAILY_LIMIT";
    private static final double LIMIT = 100_000.0;      // Rs 1,00,000 in 24 hours

    private final JdbcTemplate jdbc;

    public DailyLimitRule(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "More than Rs 1,00,000 spent by the user within 24 hours";
    }

    @Override
    public int score() {
        return 35;
    }

    @Override
    public RuleResult evaluate(TransactionEvent event) {
        Instant to = event.eventTime();
        Instant from = to.minusSeconds(24 * 60 * 60);

        Double total = jdbc.queryForObject("""
                SELECT COALESCE(SUM(amount), 0) FROM transactions
                WHERE user_id = ? AND event_time > ? AND event_time <= ?
                """,
                Double.class, event.userId(), Timestamp.from(from), Timestamp.from(to));

        if (total != null && total > LIMIT) {
            return RuleResult.flagged(NAME, 35,
                    String.format("Rs %.0f spent in the last 24 hours (limit Rs %.0f)", total, LIMIT));
        }
        return RuleResult.clean(NAME);
    }
}