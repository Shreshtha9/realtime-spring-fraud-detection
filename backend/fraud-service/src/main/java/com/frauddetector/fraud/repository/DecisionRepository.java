package com.frauddetector.fraud.repository;

import com.frauddetector.fraud.model.DecisionView;
import com.frauddetector.fraud.model.DecisionView.RuleHit;
import com.frauddetector.fraud.model.FraudDecision;
import com.frauddetector.fraud.model.RuleResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DecisionRepository {

    private final JdbcTemplate jdbc;

    public DecisionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Stores the decision (ALLOW included) and one row per rule that fired. */
    public void save(FraudDecision d) {
        int rows = jdbc.update("""
                INSERT INTO decisions (transaction_id, user_id, risk_score, decision)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (transaction_id) DO NOTHING
                """,
                d.transactionId(), d.userId(), d.riskScore(), d.decision().name());
        if (rows == 0) {
            return;     // already decided earlier, keep the first decision
        }
        for (RuleResult r : d.firedRules()) {
            jdbc.update("""
                    INSERT INTO decision_rules (transaction_id, rule_name, score, reason)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT DO NOTHING
                    """,
                    d.transactionId(), r.ruleName(), r.score(), r.reason());
        }
    }

    public Optional<DecisionView> find(String transactionId) {
        List<DecisionView> found = jdbc.query("""
                SELECT transaction_id, user_id, risk_score, decision, decided_at
                FROM decisions WHERE transaction_id = ?
                """,
                (rs, i) -> new DecisionView(
                        rs.getString("transaction_id"),
                        rs.getString("user_id"),
                        rs.getInt("risk_score"),
                        rs.getString("decision"),
                        List.of(),
                        rs.getTimestamp("decided_at").toInstant()),
                transactionId);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        List<RuleHit> hits = jdbc.query("""
                SELECT rule_name, score, reason FROM decision_rules
                WHERE transaction_id = ? ORDER BY score DESC, rule_name
                """,
                (rs, i) -> new RuleHit(rs.getString("rule_name"), rs.getInt("score"), rs.getString("reason")),
                transactionId);
        DecisionView d = found.get(0);
        return Optional.of(new DecisionView(d.transactionId(), d.userId(), d.riskScore(),
                d.decision(), hits, d.decidedAt()));
    }
}