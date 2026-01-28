package com.llmguardrails.analyzer;

import com.llmguardrails.llm.LLMService;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.RuleMatch;
import com.llmguardrails.rules.RuleEngine;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromptAnalyzer {
    
    private static final Logger log = LoggerFactory.getLogger(PromptAnalyzer.class);

    private final RuleEngine ruleEngine;
    private final LLMService llmService;

    public PromptAnalyzer(RuleEngine ruleEngine, LLMService llmService) {
        this.ruleEngine = ruleEngine;
        this.llmService = llmService;
    }
    
    public AnalysisResult analyze(String prompt) {
        log.debug("Analyzing prompt: {}", prompt.substring(0, Math.min(100, prompt.length())));
        
        // Rule-based analysis
        List<RuleMatch> blockMatches = ruleEngine.checkBlockRules(prompt);
        List<RuleMatch> redactMatches = ruleEngine.checkRedactRules(prompt);
        
        List<RuleMatch> allMatches = new ArrayList<>();
        allMatches.addAll(blockMatches);
        allMatches.addAll(redactMatches);
        
        Double ruleBasedScore = ruleEngine.calculateRuleBasedScore(allMatches);
        
        // AI-based analysis
        Double aiScore = llmService.isEnabled() ? llmService.analyzeRisk(prompt) : 0.0;
        
        // Combine scores (weighted average: 60% rules, 40% AI)
        Double combinedScore = (ruleBasedScore * 0.6) + (aiScore * 0.4);
        
        // Generate reasons
        List<String> reasons = generateReasons(allMatches, ruleBasedScore, aiScore);
        
        return new AnalysisResult(ruleBasedScore, aiScore, combinedScore, allMatches, reasons);
    }
    
    private List<String> generateReasons(List<RuleMatch> matches, Double ruleScore, Double aiScore) {
        List<String> reasons = new ArrayList<>();
        
        if (ruleScore > 0) {
            reasons.add(String.format("Rule-based score: %.2f", ruleScore));
            if (!matches.isEmpty()) {
                String matchDetails = matches.stream()
                        .map(m -> m.getRuleName() + " (severity: " + m.getSeverity() + ")")
                        .collect(Collectors.joining(", "));
                reasons.add("Matched rules: " + matchDetails);
            }
        }
        
        if (aiScore > 0 && llmService.isEnabled()) {
            reasons.add(String.format("AI risk score: %.2f", aiScore));
        }
        
        if (reasons.isEmpty()) {
            reasons.add("No security concerns detected");
        }
        
        return reasons;
    }
}

