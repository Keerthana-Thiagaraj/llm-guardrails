package com.llmguardrails.service;

import com.llmguardrails.analyzer.PromptAnalyzer;
import com.llmguardrails.decision.DecisionEngine;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.Decision;
import com.llmguardrails.model.RuleMatch;
import com.llmguardrails.model.dto.ValidationRequest;
import com.llmguardrails.model.dto.ValidationResponse;
import com.llmguardrails.redaction.RedactionService;
import com.llmguardrails.repository.IMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuardServiceTest {

    @Mock
    private PromptAnalyzer promptAnalyzer;

    @Mock
    private DecisionEngine decisionEngine;

    @Mock
    private RedactionService redactionService;

    @Mock
    private IMetricsRepository metricsRepository;

    private GuardService guardService;

    @BeforeEach
    void setUp() {
        guardService = new GuardService(
                promptAnalyzer,
                decisionEngine,
                redactionService,
                metricsRepository
        );
    }

    @Test
    void testValidate_WithBlockDecision_ShouldReturnBlock() {
        ValidationRequest request = new ValidationRequest("SELECT * FROM users");
        
        AnalysisResult analysisResult = new AnalysisResult(
                0.9, 0.8, 0.85, List.of(), List.of("High risk")
        );
        
        when(promptAnalyzer.analyze(anyString())).thenReturn(analysisResult);
        when(decisionEngine.makeDecision(analysisResult)).thenReturn(Decision.BLOCK);
        
        ValidationResponse response = guardService.validate(request);
        
        assertEquals(Decision.BLOCK, response.getDecision());
        assertEquals(0.85, response.getRiskScore());
        assertNull(response.getRedactedPrompt());
        verify(metricsRepository).recordRequest(Decision.BLOCK);
    }

    @Test
    void testValidate_WithRedactDecision_ShouldReturnRedactedPrompt() {
        ValidationRequest request = new ValidationRequest("Email: user@example.com");
        
        RuleMatch redactMatch = new RuleMatch(
                "Email Pattern", "pattern", 0.5, "Test", "user@example.com"
        );
        AnalysisResult analysisResult = new AnalysisResult(
                0.5, 0.3, 0.5, List.of(redactMatch), List.of("Medium risk")
        );
        
        when(promptAnalyzer.analyze(anyString())).thenReturn(analysisResult);
        when(decisionEngine.makeDecision(analysisResult)).thenReturn(Decision.REDACT);
        when(redactionService.redactPrompt(anyString(), anyList())).thenReturn("Email: [REDACTED]");
        
        ValidationResponse response = guardService.validate(request);
        
        assertEquals(Decision.REDACT, response.getDecision());
        assertEquals("Email: [REDACTED]", response.getRedactedPrompt());
        verify(redactionService).redactPrompt(anyString(), anyList());
        verify(metricsRepository).recordRequest(Decision.REDACT);
    }

    @Test
    void testValidate_WithAllowDecision_ShouldReturnAllow() {
        ValidationRequest request = new ValidationRequest("Hello, world!");
        
        AnalysisResult analysisResult = new AnalysisResult(
                0.2, 0.1, 0.2, List.of(), List.of("Low risk")
        );
        
        when(promptAnalyzer.analyze(anyString())).thenReturn(analysisResult);
        when(decisionEngine.makeDecision(analysisResult)).thenReturn(Decision.ALLOW);
        
        ValidationResponse response = guardService.validate(request);
        
        assertEquals(Decision.ALLOW, response.getDecision());
        assertNull(response.getRedactedPrompt());
        verify(metricsRepository).recordRequest(Decision.ALLOW);
    }

    @Test
    void testValidate_WithRedactDecision_ShouldFilterBlockRules() {
        ValidationRequest request = new ValidationRequest("Email: user@example.com");
        
        RuleMatch blockMatch = new RuleMatch("Block Rule", "pattern", 0.8, "Test", "match");
        RuleMatch redactMatch = new RuleMatch("Redact Rule", "pattern", 0.5, "Test", "match");
        
        AnalysisResult analysisResult = new AnalysisResult(
                0.5, 0.3, 0.5, List.of(blockMatch, redactMatch), List.of("Medium risk")
        );
        
        when(promptAnalyzer.analyze(anyString())).thenReturn(analysisResult);
        when(decisionEngine.makeDecision(analysisResult)).thenReturn(Decision.REDACT);
        when(redactionService.redactPrompt(anyString(), anyList())).thenReturn("Email: [REDACTED]");
        
        guardService.validate(request);
        
        // Verify only redact matches (severity < 0.7) are passed to redaction service
        verify(redactionService).redactPrompt(anyString(), argThat(matches -> 
                matches.size() == 1 && matches.get(0).getSeverity() < 0.7
        ));
    }

    @Test
    void testValidate_ShouldCallAllServicesInOrder() {
        ValidationRequest request = new ValidationRequest("Test prompt");
        AnalysisResult analysisResult = new AnalysisResult(
                0.5, 0.3, 0.5, List.of(), List.of("Test")
        );
        
        when(promptAnalyzer.analyze(anyString())).thenReturn(analysisResult);
        when(decisionEngine.makeDecision(analysisResult)).thenReturn(Decision.ALLOW);
        
        guardService.validate(request);
        
        verify(promptAnalyzer).analyze("Test prompt");
        verify(decisionEngine).makeDecision(analysisResult);
        verify(metricsRepository).recordRequest(Decision.ALLOW);
    }
}

