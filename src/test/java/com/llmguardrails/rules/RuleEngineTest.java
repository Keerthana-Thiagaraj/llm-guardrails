package com.llmguardrails.rules;

import com.llmguardrails.config.RulesConfiguration;
import com.llmguardrails.model.RuleMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleEngineTest {

    private RuleEngine ruleEngine;
    private RulesConfiguration rulesConfiguration;

    @BeforeEach
    void setUp() {
        rulesConfiguration = new RulesConfiguration();
        
        // Create test block rules
        RulesConfiguration.RuleDefinition sqlRule = new RulesConfiguration.RuleDefinition();
        sqlRule.setName("SQL Injection");
        sqlRule.setPattern("(?i)(SELECT|INSERT|DELETE|UPDATE|DROP)");
        sqlRule.setSeverity(0.9);
        sqlRule.setDescription("Detects SQL injection");
        
        RulesConfiguration.RuleDefinition xssRule = new RulesConfiguration.RuleDefinition();
        xssRule.setName("XSS Pattern");
        xssRule.setPattern("(?i)(<script|javascript:)");
        xssRule.setSeverity(0.8);
        xssRule.setDescription("Detects XSS attempts");
        
        rulesConfiguration.setBlock(List.of(sqlRule, xssRule));
        
        // Create test redact rules
        RulesConfiguration.RuleDefinition emailRule = new RulesConfiguration.RuleDefinition();
        emailRule.setName("Email Pattern");
        emailRule.setPattern("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        emailRule.setSeverity(0.5);
        emailRule.setDescription("Detects email addresses");
        
        rulesConfiguration.setRedact(List.of(emailRule));
        
        ruleEngine = new RuleEngine(rulesConfiguration);
        ruleEngine.initialize();
    }

    @Test
    void testCheckBlockRules_WithSQLInjection_ShouldMatch() {
        String prompt = "SELECT * FROM users WHERE id = 1";
        List<RuleMatch> matches = ruleEngine.checkBlockRules(prompt);
        
        assertFalse(matches.isEmpty());
        assertEquals("SQL Injection", matches.get(0).getRuleName());
        assertEquals(0.9, matches.get(0).getSeverity());
        assertTrue(matches.get(0).getMatchedText().contains("SELECT"));
    }

    @Test
    void testCheckBlockRules_WithXSS_ShouldMatch() {
        String prompt = "<script>alert('XSS')</script>";
        List<RuleMatch> matches = ruleEngine.checkBlockRules(prompt);
        
        assertFalse(matches.isEmpty());
        assertEquals("XSS Pattern", matches.get(0).getRuleName());
        assertEquals(0.8, matches.get(0).getSeverity());
    }

    @Test
    void testCheckBlockRules_WithSafePrompt_ShouldNotMatch() {
        String prompt = "Hello, how are you today?";
        List<RuleMatch> matches = ruleEngine.checkBlockRules(prompt);
        
        assertTrue(matches.isEmpty());
    }

    @Test
    void testCheckRedactRules_WithEmail_ShouldMatch() {
        String prompt = "Contact me at user@example.com";
        List<RuleMatch> matches = ruleEngine.checkRedactRules(prompt);
        
        assertFalse(matches.isEmpty());
        assertEquals("Email Pattern", matches.get(0).getRuleName());
        assertEquals(0.5, matches.get(0).getSeverity());
        assertEquals("user@example.com", matches.get(0).getMatchedText());
    }

    @Test
    void testCheckRedactRules_WithoutEmail_ShouldNotMatch() {
        String prompt = "Hello world";
        List<RuleMatch> matches = ruleEngine.checkRedactRules(prompt);
        
        assertTrue(matches.isEmpty());
    }

    @Test
    void testCalculateRuleBasedScore_WithNoMatches_ShouldReturnZero() {
        List<RuleMatch> matches = List.of();
        Double score = ruleEngine.calculateRuleBasedScore(matches);
        
        assertEquals(0.0, score);
    }

    // @Test
    // void testCalculateRuleBasedScore_WithSingleMatch_ShouldReturnSeverity() {
    //     RuleMatch match = new RuleMatch("Test Rule", "pattern", 0.7, "Test", "matched");
    //     List<RuleMatch> matches = List.of(match);
    //     Double score = ruleEngine.calculateRuleBasedScore(matches);
        
    //     assertEquals(0.7, score, 0.01);
    // }

    @Test
    void testCalculateRuleBasedScore_WithMultipleMatches_ShouldApplyPenalty() {
        RuleMatch match1 = new RuleMatch("Rule 1", "pattern1", 0.7, "Test", "matched1");
        RuleMatch match2 = new RuleMatch("Rule 2", "pattern2", 0.6, "Test", "matched2");
        List<RuleMatch> matches = List.of(match1, match2);
        Double score = ruleEngine.calculateRuleBasedScore(matches);
        
        // Should be max severity (0.7) + penalty (0.05 * 2 = 0.1) = 0.8
        assertTrue(score > 0.7);
        assertTrue(score <= 1.0);
    }

    @Test
    void testCalculateRuleBasedScore_WithHighSeverity_ShouldCapAtOne() {
        RuleMatch match = new RuleMatch("High Risk", "pattern", 0.95, "Test", "matched");
        List<RuleMatch> matches = List.of(match, match, match, match, match); // 5 matches
        Double score = ruleEngine.calculateRuleBasedScore(matches);
        
        assertTrue(score <= 1.0);
    }
}

