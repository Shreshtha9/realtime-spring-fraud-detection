package com.frauddetector.transaction.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reads the decisions table that fraud-service fills (ALLOW, REVIEW and BLOCK). */
@Repository
public class DecisionRepository {

    private final JdbcTemplate jdbc;

    public DecisionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Long> countByDecision() {
        Map<String, Long> result = new HashMap<>();
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT decision, COUNT(*) AS c FROM decisions GROUP BY decision");
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("decision"), ((Number) row.get("c")).longValue());
        }
        return result;
    }
}