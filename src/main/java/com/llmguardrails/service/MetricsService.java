package com.llmguardrails.service;

import com.llmguardrails.model.Decision;
import com.llmguardrails.model.dto.MetricsResponse;
import com.llmguardrails.repository.IMetricsRepository;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    
    private final IMetricsRepository metricsRepository;

    public MetricsService(IMetricsRepository metricsRepository) {
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

