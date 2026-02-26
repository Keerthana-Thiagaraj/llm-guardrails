package com.llmguardrails.decision;

import com.llmguardrails.config.GuardrailsConfig;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.Decision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecisionEngineTest {

    private DecisionEngine decisionEngine;
    private GuardrailsConfig guardrailsConfig;

    @BeforeEach
    void setUp() {
        guardrailsConfig = new GuardrailsConfig();
        GuardrailsConfig.DecisionConfig decisionConfig = new GuardrailsConfig.DecisionConfig();
        decisionConfig.setBlockThreshold(0.7);
        decisionConfig.setRedactThreshold(0.4);
        decisionConfig.setAllowThreshold(0.0);
        guardrailsConfig.setDecision(decisionConfig);
        
        decisionEngine = new DecisionEngine(guardrailsConfig);
    }

    @Test
    void testMakeDecision_WithHighRiskScore_ShouldBlock() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.9, 0.8, 0.85, List.of(), List.of("High risk detected")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.BLOCK, decision);
    }

    @Test
    void testMakeDecision_WithMediumRiskScore_ShouldRedact() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.5, 0.3, 0.5, List.of(), List.of("Medium risk detected")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.REDACT, decision);
    }

    @Test
    void testMakeDecision_WithLowRiskScore_ShouldAllow() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.2, 0.1, 0.2, List.of(), List.of("Low risk")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.ALLOW, decision);
    }

    @Test
    void testMakeDecision_WithExactBlockThreshold_ShouldBlock() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.7, 0.7, 0.7, List.of(), List.of("At threshold")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.BLOCK, decision);
    }

    @Test
    void testMakeDecision_WithExactRedactThreshold_ShouldRedact() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.4, 0.4, 0.4, List.of(), List.of("At threshold")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.REDACT, decision);
    }

    @Test
    void testMakeDecision_WithZeroRiskScore_ShouldAllow() {
        AnalysisResult analysisResult = new AnalysisResult(
                0.0, 0.0, 0.0, List.of(), List.of("No risk")
        );
        
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        assertEquals(Decision.ALLOW, decision);
    }
}

