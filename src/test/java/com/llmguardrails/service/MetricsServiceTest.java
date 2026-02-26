package com.llmguardrails.service;

import com.llmguardrails.model.Decision;
import com.llmguardrails.model.dto.MetricsResponse;
import com.llmguardrails.repository.IMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricsServiceTest {

    private IMetricsRepository metricsRepository;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsRepository = mock(IMetricsRepository.class);
        metricsService = new MetricsService(metricsRepository);
    }

    @Test
    void testGetMetrics_ShouldReturnAllCounts() {
        when(metricsRepository.getTotalRequests()).thenReturn(100L);
        when(metricsRepository.getCount(Decision.ALLOW)).thenReturn(70L);
        when(metricsRepository.getCount(Decision.BLOCK)).thenReturn(20L);
        when(metricsRepository.getCount(Decision.REDACT)).thenReturn(10L);
        
        MetricsResponse metrics = metricsService.getMetrics();
        
        assertNotNull(metrics);
        assertEquals(100L, metrics.getTotalRequests());
        assertEquals(70L, metrics.getAllowed());
        assertEquals(20L, metrics.getBlocked());
        assertEquals(10L, metrics.getRedacted());
    }

    @Test
    void testGetMetrics_WithZeroRequests_ShouldReturnZeros() {
        when(metricsRepository.getTotalRequests()).thenReturn(0L);
        when(metricsRepository.getCount(Decision.ALLOW)).thenReturn(0L);
        when(metricsRepository.getCount(Decision.BLOCK)).thenReturn(0L);
        when(metricsRepository.getCount(Decision.REDACT)).thenReturn(0L);
        
        MetricsResponse metrics = metricsService.getMetrics();
        
        assertEquals(0L, metrics.getTotalRequests());
        assertEquals(0L, metrics.getAllowed());
        assertEquals(0L, metrics.getBlocked());
        assertEquals(0L, metrics.getRedacted());
    }
}

