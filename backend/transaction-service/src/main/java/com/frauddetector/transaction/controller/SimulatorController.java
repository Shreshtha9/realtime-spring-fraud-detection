package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.generator.TrafficGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final TrafficGenerator generator;

    public SimulatorController(TrafficGenerator generator) {
        this.generator = generator;
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
}