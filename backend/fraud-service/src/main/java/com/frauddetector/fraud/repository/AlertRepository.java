package com.frauddetector.fraud.repository;

import com.frauddetector.fraud.model.AlertMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AlertRepository {

    private final JdbcTemplate jdbc;

    public AlertRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(AlertMessage a) {
        jdbc.update("""
                INSERT INTO alerts (transaction_id, user_id, risk_score, decision, reasons)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (transaction_id) DO NOTHING
                """,
                a.transactionId(), a.userId(), a.riskScore(), a.decision(),
                String.join("\n", a.reasons()));
    }
}