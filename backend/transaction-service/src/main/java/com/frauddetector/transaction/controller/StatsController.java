package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.repository.AlertRepository;
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

    public StatsController(AlertRepository alerts, TransactionRepository transactions) {
        this.alerts = alerts;
        this.transactions = transactions;
    }

    @GetMapping
    public Map<String, Long> stats() {
        Map<String, Long> byDecision = alerts.countByDecision();
        long review = byDecision.getOrDefault("REVIEW", 0L);
        long blocked = byDecision.getOrDefault("BLOCK", 0L);
        return Map.of(
                "totalTransactions", transactions.count(),
                "totalAlerts", review + blocked,
                "review", review,
                "blocked", blocked);
    }
}