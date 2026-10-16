package com.frauddetector.transaction.repository;

import com.frauddetector.transaction.model.AlertView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AlertRepository {

    private final JdbcTemplate jdbc;

    public AlertRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<AlertView> findRecent(int limit) {
        return jdbc.query("""
                SELECT transaction_id, user_id, risk_score, decision, reasons, created_at
                FROM alerts
                ORDER BY id DESC
                LIMIT ?
                """,
                (rs, i) -> {
                    String reasons = rs.getString("reasons");
                    return new AlertView(
                            rs.getString("transaction_id"),
                            rs.getString("user_id"),
                            rs.getInt("risk_score"),
                            rs.getString("decision"),
                            reasons == null ? List.of() : Arrays.asList(reasons.split("\n")),
                            rs.getTimestamp("created_at").toInstant());
                },
                limit);
    }

    public Map<String, Long> countByDecision() {
        Map<String, Long> result = new HashMap<>();
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT decision, COUNT(*) AS c FROM alerts GROUP BY decision");
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("decision"), ((Number) row.get("c")).longValue());
        }
        return result;
    }
}