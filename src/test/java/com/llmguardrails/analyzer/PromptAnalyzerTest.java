package com.llmguardrails.analyzer;

import com.llmguardrails.llm.LLMService;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.RuleMatch;
import com.llmguardrails.rules.RuleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptAnalyzerTest {

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private LLMService llmService;

    private PromptAnalyzer promptAnalyzer;

    @BeforeEach
    void setUp() {
        promptAnalyzer = new PromptAnalyzer(ruleEngine, llmService);
    }

    @Test
    void testAnalyze_WithRuleMatches_ShouldCalculateRuleBasedScore() {
        String prompt = "SELECT * FROM users";
        
        RuleMatch match = new RuleMatch("SQL Injection", "pattern", 0.9, "Test", "SELECT");
        when(ruleEngine.checkBlockRules(prompt)).thenReturn(List.of(match));
        when(ruleEngine.checkRedactRules(prompt)).thenReturn(List.of());
        when(ruleEngine.calculateRuleBasedScore(anyList())).thenReturn(0.9);
        when(llmService.isEnabled()).thenReturn(false);
        
        AnalysisResult result = promptAnalyzer.analyze(prompt);
        
        assertNotNull(result);
        assertEquals(0.9, result.getRuleBasedScore());
        assertEquals(0.0, result.getAiScore());
        assertFalse(result.getRuleMatches().isEmpty());
        assertFalse(result.getReasons().isEmpty());
    }

    @Test
    void testAnalyze_WithAIServiceEnabled_ShouldIncludeAIScore() {
        String prompt = "Hello world";
        
        when(ruleEngine.checkBlockRules(prompt)).thenReturn(List.of());
        when(ruleEngine.checkRedactRules(prompt)).thenReturn(List.of());
        when(ruleEngine.calculateRuleBasedScore(anyList())).thenReturn(0.0);
        when(llmService.isEnabled()).thenReturn(true);
        when(llmService.analyzeRisk(prompt)).thenReturn(0.5);
        
        AnalysisResult result = promptAnalyzer.analyze(prompt);
        
        assertNotNull(result);
        assertEquals(0.0, result.getRuleBasedScore());
        assertEquals(0.5, result.getAiScore());
        // Combined: 0.0 * 0.6 + 0.5 * 0.4 = 0.2
        assertEquals(0.2, result.getCombinedScore(), 0.01);
    }

    @Test
    void testAnalyze_WithBothRuleAndAI_ShouldCombineScores() {
        String prompt = "SELECT * FROM users";
        
        RuleMatch match = new RuleMatch("SQL Injection", "pattern", 0.9, "Test", "SELECT");
        when(ruleEngine.checkBlockRules(prompt)).thenReturn(List.of(match));
        when(ruleEngine.checkRedactRules(prompt)).thenReturn(List.of());
        when(ruleEngine.calculateRuleBasedScore(anyList())).thenReturn(0.9);
        when(llmService.isEnabled()).thenReturn(true);
        when(llmService.analyzeRisk(prompt)).thenReturn(0.7);
        
        AnalysisResult result = promptAnalyzer.analyze(prompt);
        
        assertNotNull(result);
        assertEquals(0.9, result.getRuleBasedScore());
        assertEquals(0.7, result.getAiScore());
        // Combined: 0.9 * 0.6 + 0.7 * 0.4 = 0.54 + 0.28 = 0.82
        assertEquals(0.82, result.getCombinedScore(), 0.01);
    }

    @Test
    void testAnalyze_WithNoMatches_ShouldReturnZeroScore() {
        String prompt = "Hello, how are you?";
        
        when(ruleEngine.checkBlockRules(prompt)).thenReturn(List.of());
        when(ruleEngine.checkRedactRules(prompt)).thenReturn(List.of());
        when(ruleEngine.calculateRuleBasedScore(anyList())).thenReturn(0.0);
        when(llmService.isEnabled()).thenReturn(false);
        
        AnalysisResult result = promptAnalyzer.analyze(prompt);
        
        assertNotNull(result);
        assertEquals(0.0, result.getRuleBasedScore());
        assertEquals(0.0, result.getCombinedScore());
        assertTrue(result.getReasons().contains("No security concerns detected"));
    }

    @Test
    void testAnalyze_ShouldIncludeAllMatches() {
        String prompt = "SELECT * FROM users";
        
        RuleMatch blockMatch = new RuleMatch("SQL Injection", "pattern1", 0.9, "Test", "SELECT");
        RuleMatch redactMatch = new RuleMatch("Email Pattern", "pattern2", 0.5, "Test", "email");
        
        when(ruleEngine.checkBlockRules(prompt)).thenReturn(List.of(blockMatch));
        when(ruleEngine.checkRedactRules(prompt)).thenReturn(List.of(redactMatch));
        when(ruleEngine.calculateRuleBasedScore(anyList())).thenReturn(0.9);
        when(llmService.isEnabled()).thenReturn(false);
        
        AnalysisResult result = promptAnalyzer.analyze(prompt);
        
        assertEquals(2, result.getRuleMatches().size());
    }
}

