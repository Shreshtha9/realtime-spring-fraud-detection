package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.generator.ScenarioService;
import com.frauddetector.transaction.generator.TrafficGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final TrafficGenerator generator;
    private final ScenarioService scenarios;

    public SimulatorController(TrafficGenerator generator, ScenarioService scenarios) {
        this.generator = generator;
        this.scenarios = scenarios;
    }

    @PostMapping("/start")
    public Map<String, Object> start() {
        generator.start();
        return Map.of("running", true);
    }

    @PostMapping("/stop")
    public Map<String, Object> stop() {
        generator.stop();
        return Map.of("running", false);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("running", generator.isRunning());
    }

    @PostMapping("/scenario/{type}")
    public Map<String, Object> scenario(@PathVariable String type) {
        try {
            String userId = scenarios.run(type);
            return Map.of("scenario", type, "userId", userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}