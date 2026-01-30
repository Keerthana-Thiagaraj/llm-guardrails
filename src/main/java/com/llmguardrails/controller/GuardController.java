package com.llmguardrails.controller;

import com.llmguardrails.model.dto.MetricsResponse;
import com.llmguardrails.model.dto.ValidationRequest;
import com.llmguardrails.model.dto.ValidationResponse;
import com.llmguardrails.service.GuardService;
import com.llmguardrails.service.MetricsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/guard")
public class GuardController {
    
    private static final Logger log = LoggerFactory.getLogger(GuardController.class);

    private final GuardService guardService;
    private final MetricsService metricsService;

    public GuardController(GuardService guardService, MetricsService metricsService) {
        this.guardService = guardService;
        this.metricsService = metricsService;
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@Valid @RequestBody ValidationRequest request) {
        log.info("Received validation request");
        ValidationResponse response = guardService.validate(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> getMetrics() {
        log.debug("Retrieving metrics");
        MetricsResponse metrics = metricsService.getMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}

