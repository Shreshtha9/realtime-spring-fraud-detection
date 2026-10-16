package com.frauddetector.transaction.controller;

import com.frauddetector.transaction.model.AlertView;
import com.frauddetector.transaction.repository.AlertRepository;
import com.frauddetector.transaction.stream.AlertStreamHub;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alerts;
    private final AlertStreamHub hub;

    public AlertController(AlertRepository alerts, AlertStreamHub hub) {
        this.alerts = alerts;
        this.hub = hub;
    }

    @GetMapping
    public List<AlertView> recent(@RequestParam(name = "limit", defaultValue = "50") int limit) {
        return alerts.findRecent(Math.min(limit, 200));
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return hub.subscribe();
    }
}