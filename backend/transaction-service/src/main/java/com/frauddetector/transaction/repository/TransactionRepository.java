package com.frauddetector.transaction.repository;

import com.frauddetector.transaction.model.TransactionRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbc;

    public TransactionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long count() {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM transactions", Long.class);
        return n == null ? 0 : n;
    }

    public List<TransactionRow> findRecent(int limit) {
        return jdbc.query("""
                SELECT transaction_id, user_id, amount, city, device_id, event_time
                FROM transactions
                ORDER BY event_time DESC
                LIMIT ?
                """,
                (rs, i) -> new TransactionRow(
                        rs.getString("transaction_id"),
                        rs.getString("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("city"),
                        rs.getString("device_id"),
                        rs.getTimestamp("event_time").toInstant()),
                limit);
    }
}