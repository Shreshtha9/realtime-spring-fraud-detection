package com.frauddetector.fraud.model;

public record RuleResult(String ruleName, boolean triggered, int score, String reason) {

    public static RuleResult clean(String ruleName) {
        return new RuleResult(ruleName, false, 0, "");
    }

    public static RuleResult flagged(String ruleName, int score, String reason) {
        return new RuleResult(ruleName, true, score, reason);
    }
}