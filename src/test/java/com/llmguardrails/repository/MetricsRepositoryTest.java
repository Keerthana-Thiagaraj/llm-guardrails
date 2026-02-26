package com.llmguardrails.repository;

import com.llmguardrails.model.Decision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsRepositoryTest {

    private MetricsRepository metricsRepository;

    @BeforeEach
    void setUp() {
        metricsRepository = new MetricsRepository();
    }

    @Test
    void testRecordRequest_ShouldIncrementCounters() {
        metricsRepository.recordRequest(Decision.ALLOW);
        metricsRepository.recordRequest(Decision.BLOCK);
        metricsRepository.recordRequest(Decision.REDACT);
        
        assertEquals(3L, metricsRepository.getTotalRequests());
        assertEquals(1L, metricsRepository.getCount(Decision.ALLOW));
        assertEquals(1L, metricsRepository.getCount(Decision.BLOCK));
        assertEquals(1L, metricsRepository.getCount(Decision.REDACT));
    }

    @Test
    void testRecordRequest_WithMultipleSameDecision_ShouldAccumulate() {
        metricsRepository.recordRequest(Decision.ALLOW);
        metricsRepository.recordRequest(Decision.ALLOW);
        metricsRepository.recordRequest(Decision.ALLOW);
        
        assertEquals(3L, metricsRepository.getTotalRequests());
        assertEquals(3L, metricsRepository.getCount(Decision.ALLOW));
        assertEquals(0L, metricsRepository.getCount(Decision.BLOCK));
    }

    @Test
    void testGetCount_WithNoRecords_ShouldReturnZero() {
        assertEquals(0L, metricsRepository.getCount(Decision.ALLOW));
        assertEquals(0L, metricsRepository.getCount(Decision.BLOCK));
        assertEquals(0L, metricsRepository.getCount(Decision.REDACT));
        assertEquals(0L, metricsRepository.getTotalRequests());
    }

    @Test
    void testReset_ShouldClearAllCounters() {
        metricsRepository.recordRequest(Decision.ALLOW);
        metricsRepository.recordRequest(Decision.BLOCK);
        
        metricsRepository.reset();
        
        assertEquals(0L, metricsRepository.getTotalRequests());
        assertEquals(0L, metricsRepository.getCount(Decision.ALLOW));
        assertEquals(0L, metricsRepository.getCount(Decision.BLOCK));
    }
}

