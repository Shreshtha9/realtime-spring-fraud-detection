package com.frauddetector.fraud.controller;

import com.frauddetector.fraud.rules.FraudRule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    public record RuleInfo(String name, String description, int score) {
    }

    private final List<FraudRule> rules;

    public RuleController(List<FraudRule> rules) {
        this.rules = rules;
    }

    @GetMapping
    public List<RuleInfo> list() {
        return rules.stream()
                .map(r -> new RuleInfo(r.name(), r.description(), r.score()))
                .sorted(Comparator.comparingInt(RuleInfo::score).reversed())
                .toList();
    }
}