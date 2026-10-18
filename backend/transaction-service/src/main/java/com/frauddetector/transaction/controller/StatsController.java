package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.repository.AlertRepository;
import com.frauddetector.transaction.repository.DecisionRepository;
import com.frauddetector.transaction.repository.TransactionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final AlertRepository alerts;
    private final TransactionRepository transactions;
    private final DecisionRepository decisions;

    public StatsController(AlertRepository alerts,
                           TransactionRepository transactions,
                           DecisionRepository decisions) {
        this.alerts = alerts;
        this.transactions = transactions;
        this.decisions = decisions;
    }

    @GetMapping
    public Map<String, Long> stats() {
        Map<String, Long> byDecision = alerts.countByDecision();
        long review = byDecision.getOrDefault("REVIEW", 0L);
        long blocked = byDecision.getOrDefault("BLOCK", 0L);
        long allowed = decisions.countByDecision().getOrDefault("ALLOW", 0L);   // new: from decisions table
        return Map.of(
                "totalTransactions", transactions.count(),
                "totalAlerts", review + blocked,
                "allowed", allowed,
                "review", review,
                "blocked", blocked);
    }
}