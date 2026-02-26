package com.llmguardrails.llm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockLLMServiceTest {

    private MockLLMService mockLLMService;

    @BeforeEach
    void setUp() {
        mockLLMService = new MockLLMService();
    }

    @Test
    void testAnalyzeRisk_ShouldAlwaysReturnZero() {
        Double riskScore = mockLLMService.analyzeRisk("SELECT * FROM users");
        
        assertEquals(0.0, riskScore);
    }

    @Test
    void testIsEnabled_ShouldAlwaysReturnTrue() {
        assertTrue(mockLLMService.isEnabled());
    }

    @Test
    void testAnalyzeRisk_WithDifferentPrompts_ShouldReturnZero() {
        assertEquals(0.0, mockLLMService.analyzeRisk("Safe prompt"));
        assertEquals(0.0, mockLLMService.analyzeRisk("Dangerous prompt"));
        assertEquals(0.0, mockLLMService.analyzeRisk(""));
    }
}

