package com.llmguardrails.rules;

import com.llmguardrails.config.RulesConfiguration;
import com.llmguardrails.model.RuleMatch;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RuleEngine {
    
    private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);

    private final RulesConfiguration rulesConfiguration;
    
    private List<CompiledRule> blockRules;
    private List<CompiledRule> redactRules;

    public RuleEngine(RulesConfiguration rulesConfiguration) {
        this.rulesConfiguration = rulesConfiguration;
    }
    
    public void initialize() {
        log.info("Initializing rule engine...");
        blockRules = compileRules(rulesConfiguration.getBlock());
        redactRules = compileRules(rulesConfiguration.getRedact());
        log.info("Rule engine initialized: {} block rules, {} redact rules", 
                blockRules.size(), redactRules.size());
    }
    
    private List<CompiledRule> compileRules(List<RulesConfiguration.RuleDefinition> definitions) {
        List<CompiledRule> compiled = new ArrayList<>();
        for (RulesConfiguration.RuleDefinition def : definitions) {
            try {
                Pattern pattern = Pattern.compile(def.getPattern());
                compiled.add(new CompiledRule(def.getName(), pattern, def.getSeverity(), def.getDescription()));
            } catch (Exception e) {
                log.error("Failed to compile rule: {}", def.getName(), e);
            }
        }
        return compiled;
    }
    
    public List<RuleMatch> checkBlockRules(String prompt) {
        return checkRules(prompt, blockRules);
    }
    
    public List<RuleMatch> checkRedactRules(String prompt) {
        return checkRules(prompt, redactRules);
    }
    
    private List<RuleMatch> checkRules(String prompt, List<CompiledRule> rules) {
        List<RuleMatch> matches = new ArrayList<>();
        
        for (CompiledRule rule : rules) {
            Matcher matcher = rule.pattern.matcher(prompt);
            while (matcher.find()) {
                String matchedText = matcher.group();
                matches.add(new RuleMatch(
                        rule.name,
                        rule.pattern.pattern(),
                        rule.severity,
                        rule.description,
                        matchedText
                ));
            }
        }
        
        return matches;
    }
    
    public Double calculateRuleBasedScore(List<RuleMatch> matches) {
        if (matches.isEmpty()) {
            return 0.0;
        }
        
        // Use maximum severity as the base score
        double maxSeverity = matches.stream()
                .mapToDouble(RuleMatch::getSeverity)
                .max()
                .orElse(0.0);
        
        // Apply penalty for multiple matches
        double penalty = Math.min(0.2, matches.size() * 0.05);
        
        return Math.min(1.0, maxSeverity + penalty);
    }
    
    private record CompiledRule(String name, Pattern pattern, Double severity, String description) {}
}

