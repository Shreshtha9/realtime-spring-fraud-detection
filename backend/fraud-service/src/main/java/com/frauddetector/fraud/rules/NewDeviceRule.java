package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class NewDeviceRule implements FraudRule {

    private static final String NAME = "NEW_DEVICE";

    private final JdbcTemplate jdbc;

    public NewDeviceRule(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

       @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "Payment from a device the user has never used before";
    }

    @Override
    public int score() {
        return 30;
    }

    @Override
    public RuleResult evaluate(TransactionEvent event) {
        Integer known = jdbc.queryForObject(
                "SELECT COUNT(*) FROM known_devices WHERE user_id = ? AND device_id = ?",
                Integer.class, event.userId(), event.deviceId());

        if (known != null && known == 0) {
            return RuleResult.flagged(NAME, 30,
                    "Device " + event.deviceId() + " has never been used by this user");
        }
        return RuleResult.clean(NAME);
    }
}