package com.llmguardrails.service;

import com.llmguardrails.model.Decision;
import com.llmguardrails.model.dto.MetricsResponse;
import com.llmguardrails.repository.MetricsRepository;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    
    private final MetricsRepository metricsRepository;

    public MetricsService(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }
    
    public MetricsResponse getMetrics() {
        return new MetricsResponse(
                metricsRepository.getTotalRequests(),
                metricsRepository.getCount(Decision.ALLOW),
                metricsRepository.getCount(Decision.BLOCK),
                metricsRepository.getCount(Decision.REDACT)
        );
    }
}

