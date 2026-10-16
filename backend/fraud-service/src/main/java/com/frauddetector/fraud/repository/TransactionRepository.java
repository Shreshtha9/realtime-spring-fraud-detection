package com.frauddetector.fraud.repository;

import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbc;

    public TransactionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Returns true if the payment was new, false if we had already seen this transactionId. */
    public boolean saveIfNew(TransactionEvent e) {
        int rows = jdbc.update("""
                INSERT INTO transactions (transaction_id, user_id, amount, city, device_id, event_time)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (transaction_id) DO NOTHING
                """,
                e.transactionId(), e.userId(), e.amount(), e.city(), e.deviceId(),
                Timestamp.from(e.eventTime()));
        return rows == 1;
    }
}