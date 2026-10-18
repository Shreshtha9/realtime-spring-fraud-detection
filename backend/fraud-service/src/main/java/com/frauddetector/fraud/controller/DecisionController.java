package com.frauddetector.fraud.controller;

import com.frauddetector.fraud.model.DecisionView;
import com.frauddetector.fraud.repository.DecisionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decisions")
public class DecisionController {

    private final DecisionRepository decisions;

    public DecisionController(DecisionRepository decisions) {
        this.decisions = decisions;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<DecisionView> get(@PathVariable String transactionId) {
        return decisions.find(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}