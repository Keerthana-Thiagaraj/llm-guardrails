package com.llmguardrails.repository;

import com.llmguardrails.model.Decision;

public interface IMetricsRepository {
    void recordRequest(Decision decision);
    Long getTotalRequests();
    Long getCount(Decision decision);
    void reset();
}

